;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.
;;
;; Copyright (c) 2015-2016 Andrey Antukh <niwi@niwi.nz>
;; Copyright (c) 2015-2016 Juan de la Cruz <delacruzgarciajuan@gmail.com>

(ns uxbox.data.images
  (:require [cuerdas.core :as str]
            [beicon.core :as rx]
            [uuid.core :as uuid]
            [uxbox.rstore :as rs]
            [uxbox.state :as st]
            [uxbox.state.images :as sti]
            [uxbox.repo :as rp]))

;; --- Initialize

(declare fetch-collections)
(declare collections-fetched?)

(defrecord Initialize []
  rs/EffectEvent
  (-apply-effect [_ state]
    (when-not (seq (:images-by-id state))
      (reset! st/loader true)))

  rs/WatchEvent
  (-apply-watch [_ state s]
    (let [images (seq (:images-by-id state))]
      (if images
        (rx/empty)
        (rx/merge
         (rx/of (fetch-collections))
         (->> (rx/filter collections-fetched? s)
              (rx/take 1)
              (rx/do #(reset! st/loader false))
              (rx/ignore)))))))

(defn initialize
  []
  (Initialize.))

;; --- Color Collections Fetched

(defrecord CollectionsFetched [items]
  rs/UpdateEvent
  (-apply-update [_ state]
    (reduce sti/assoc-collection state items)))

(defn collections-fetched
  [items]
  (CollectionsFetched. items))

;; --- Fetch Color Collections

(defrecord FetchCollections []
  rs/WatchEvent
  (-apply-watch [_ state s]
    (->> (rp/req :fetch/image-collections)
         (rx/map :payload)
         (rx/map collections-fetched))))

(defn fetch-collections
  []
  (FetchCollections.))

;; --- Collection Created

(defrecord CollectionCreated [item]
  rs/UpdateEvent
  (-apply-update [_ state]
    (-> state
        (sti/assoc-collection item)
        (assoc-in [:dashboard :collection-id] (:id item))
        (assoc-in [:dashboard :collection-type] :own))))

(defn collection-created
  [item]
  (CollectionCreated. item))

;; --- Create Collection

(defrecord CreateCollection []
  rs/WatchEvent
  (-apply-watch [_ state s]
    (let [coll {:name "Unnamed collection"
                :id (uuid/random)}]
      (->> (rp/req :create/image-collection coll)
           (rx/map :payload)
           (rx/map collection-created)))))

(defn create-collection
  []
  (CreateCollection.))

(defn collections-fetched?
  [v]
  (instance? CollectionFetched v))

;; --- Collection Updated

(defrecord CollectionUpdated [item]
  rs/UpdateEvent
  (-apply-update [_ state]
    (sti/assoc-collection state item)))

(defn collection-updated
  [item]
  (CollectionUpdated. item))

;; --- Update Collection

(defrecord UpdateCollection [id]
  rs/WatchEvent
  (-apply-watch [_ state s]
    (let [item (get-in state [:images-by-id id])]
      (->> (rp/req :update/image-collection item)
           (rx/map :payload)
           (rx/map collection-updated)))))

(defn update-collection
  [id]
  (UpdateCollection. id))

;; --- Rename Collection

(defrecord RenameCollection [id name]
  rs/UpdateEvent
  (-apply-update [_ state]
    (assoc-in state [:images-by-id id :name] name))

  rs/WatchEvent
  (-apply-watch [_ state s]
    (rx/of (update-collection id))))

(defn rename-collection
  [item name]
  (RenameCollection. item name))

;; --- Delete Collection

(defrecord DeleteCollection [id]
  rs/UpdateEvent
  (-apply-update [_ state]
    (sti/dissoc-collection state id))

  rs/WatchEvent
  (-apply-watch [_ state s]
    (->> (rp/req :delete/image-collection id)
         (rx/ignore))))

(defn delete-collection
  [id]
  (DeleteCollection. id))

;; --- Image Created

(defrecord ImageCreated [item]
  rs/UpdateEvent
  (-apply-update [_ state]
    (update-in state [:images-by-id (:collection item) :images]
               #(conj % item))))

(defn image-created
  [item]
  (ImageCreated. item))

;; --- Create Image

(defrecord CreateImages [coll-id files]
  rs/WatchEvent
  (-apply-watch [_ state s]
    (let [image-data {:coll coll-id
                      :id (uuid/random)
                      :files files}]
      (->> (rp/req :create/image image-data)
           (rx/map :payload)
           (rx/map image-created)))))

(defn create-images
  [coll-id files]
  (CreateImages. coll-id files))

;; --- Images Loaded

(defrecord ImagesLoaded [coll-id items]
  rs/UpdateEvent
  (-apply-update [_ state]
    (assoc-in state [:images-by-id coll-id :images] (set items))))

(defn images-loaded
  [coll-id items]
  (ImagesLoaded. coll-id items))

;; --- Load Images

(defrecord LoadImages [coll-id]
  rs/WatchEvent
  (-apply-watch [_ state s]
    (let [params {:coll coll-id}]
      (->> (rp/req :fetch/images params)
           (rx/map :payload)
           (rx/map #(images-loaded coll-id %))))))

(defn load-images
  [coll-id]
  (LoadImages. coll-id))

;; --- Delete Images

(defrecord DeleteImage [coll-id image]
  rs/UpdateEvent
  (-apply-update [_ state]
    (sti/dissoc-image state coll-id image))

  rs/WatchEvent
  (-apply-watch [_ state s]
    (->> (rp/req :delete/image (:id image))
         (rx/ignore))))

(defn delete-image
  [coll-id image]
  (DeleteImage. coll-id image))

;; --- Set Collection

(defrecord SetCollection [id]
  rs/UpdateEvent
  (-apply-update [_ state]
    (assoc-in state [:dashboard :collection-id] id))

  rs/WatchEvent
  (-apply-watch [_ state s]
    (rx/of (load-images id))))

(defn set-collection
  [id]
  (SetCollection. id))

;; --- Set Collection Type

(defrecord SetCollectionType [type]
  rs/WatchEvent
  (-apply-watch [_ state s]
    (if (= type :builtin)
      (rx/of (set-collection 1))
      (let [colls (sort-by :id (vals (:images-by-id state)))]
        (rx/of (set-collection (:id (first colls)))))))

  rs/UpdateEvent
  (-apply-update [_ state]
    (as-> state $
      (assoc-in $ [:dashboard :collection-type] type))))

(defn set-collection-type
  [type]
  {:pre [(contains? #{:builtin :own} type)]}
  (SetCollectionType. type))

;; --- Helpers

(defn set-images-ordering
  [order]
  (reify
    rs/UpdateEvent
    (-apply-update [_ state]
      (assoc-in state [:dashboard :images-order] order))))

(defn set-images-filtering
  [term]
  (reify
    rs/UpdateEvent
    (-apply-update [_ state]
      (assoc-in state [:dashboard :images-filter] term))))

(defn clear-images-filtering
  []
  (reify
    rs/UpdateEvent
    (-apply-update [_ state]
      (assoc-in state [:dashboard :images-filter] ""))))
