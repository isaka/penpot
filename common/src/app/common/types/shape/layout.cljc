;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.
;;
;; Copyright (c) KALEIDOS INC

(ns app.common.types.shape.layout
  (:require
   [app.common.data :as d]
   [app.common.data.macros :as dm]
   [app.common.spec :as us]
   [app.common.uuid :as uuid]
   [clojure.spec.alpha :as s]))

;; :layout                 ;; :flex, :grid in the future
;; :layout-flex-dir        ;; :row, :row-reverse, :column, :column-reverse
;; :layout-gap-type        ;; :simple, :multiple
;; :layout-gap             ;; {:row-gap number , :column-gap number}

;; :layout-align-items     ;; :start :end :center :stretch
;; :layout-justify-content ;; :start :center :end :space-between :space-around :space-evenly
;; :layout-align-content   ;; :start :center :end :space-between :space-around :space-evenly :stretch (by default)
;; :layout-wrap-type       ;; :wrap, :nowrap
;; :layout-padding-type    ;; :simple, :multiple
;; :layout-padding         ;; {:p1 num :p2 num :p3 num :p4 num} number could be negative

;; layout-grid-rows
;; layout-grid-columns
;; layout-justify-items

;; ITEMS
;; :layout-item-margin      ;; {:m1 0 :m2 0 :m3 0 :m4 0}
;; :layout-item-margin-type ;; :simple :multiple
;; :layout-item-h-sizing    ;; :fill :fix :auto
;; :layout-item-v-sizing    ;; :fill :fix :auto
;; :layout-item-max-h       ;; num
;; :layout-item-min-h       ;; num
;; :layout-item-max-w       ;; num
;; :layout-item-min-w       ;; num
;; :layout-item-absolute
;; :layout-item-z-index

(s/def ::layout  #{:flex :grid})

(s/def ::layout-flex-dir #{:row :reverse-row :row-reverse :column :reverse-column :column-reverse}) ;;TODO remove reverse-column and reverse-row after script
(s/def ::layout-grid-dir #{:row :column})
(s/def ::layout-gap-type #{:simple :multiple})
(s/def ::layout-gap ::us/safe-number)

(s/def ::layout-align-items #{:start :end :center :stretch})
(s/def ::layout-justify-items #{:start :end :center :stretch})
(s/def ::layout-align-content #{:start :end :center :space-between :space-around :space-evenly :stretch})
(s/def ::layout-justify-content #{:start :center :end :space-between :space-around :space-evenly})
(s/def ::layout-wrap-type #{:wrap :nowrap :no-wrap}) ;;TODO remove no-wrap after script
(s/def ::layout-padding-type #{:simple :multiple})

(s/def :grid/type #{:percent :flex :auto :fixed})
(s/def :grid/value (s/nilable ::us/safe-number))
(s/def ::grid-definition (s/keys :req-un [:grid/type]
                                 :opt-un [:grid/value]))
(s/def ::layout-grid-rows (s/coll-of ::grid-definition :kind vector?))
(s/def ::layout-grid-columns (s/coll-of ::grid-definition :kind vector?))

(s/def :grid-cell/id uuid?)
(s/def :grid-cell/area-name ::us/string)
(s/def :grid-cell/row-start ::us/safe-integer)
(s/def :grid-cell/row-span ::us/safe-integer)
(s/def :grid-cell/column-start ::us/safe-integer)
(s/def :grid-cell/column-span ::us/safe-integer)
(s/def :grid-cell/position #{:auto :manual :area})
(s/def :grid-cell/align-self #{:auto :start :end :center :stretch})
(s/def :grid-cell/justify-self #{:auto :start :end :center :stretch})
(s/def :grid-cell/shapes (s/coll-of uuid?))

(s/def ::grid-cell (s/keys :opt-un [:grid-cell/id
                                    :grid-cell/area-name
                                    :grid-cell/row-start
                                    :grid-cell/row-span
                                    :grid-cell/column-start
                                    :grid-cell/column-span
                                    :grid-cell/position ;; auto, manual, area
                                    :grid-cell/align-self
                                    :grid-cell/justify-self
                                    :grid-cell/shapes]))
(s/def ::layout-grid-cells (s/map-of uuid? ::grid-cell))

(s/def ::p1 ::us/safe-number)
(s/def ::p2 ::us/safe-number)
(s/def ::p3 ::us/safe-number)
(s/def ::p4 ::us/safe-number)

(s/def ::layout-padding
  (s/keys :opt-un [::p1 ::p2 ::p3 ::p4]))

(s/def ::row-gap ::us/safe-number)
(s/def ::column-gap ::us/safe-number)

(s/def ::layout-gap
  (s/keys :opt-un [::row-gap ::column-gap]))

(s/def ::layout-container-props
  (s/keys :opt-un [::layout
                   ::layout-flex-dir
                   ::layout-gap
                   ::layout-gap-type
                   ::layout-wrap-type
                   ::layout-padding-type
                   ::layout-padding
                   ::layout-justify-content
                   ::layout-align-items
                   ::layout-align-content

                   ;; grid
                   ::layout-grid-dir
                   ::layout-justify-items
                   ::layout-grid-rows
                   ::layout-grid-columns
                   ::layout-grid-cells
                   ]))

(s/def ::m1 ::us/safe-number)
(s/def ::m2 ::us/safe-number)
(s/def ::m3 ::us/safe-number)
(s/def ::m4 ::us/safe-number)

(s/def ::layout-item-margin (s/keys :opt-un [::m1 ::m2 ::m3 ::m4]))

(s/def ::layout-item-margin-type #{:simple :multiple})
(s/def ::layout-item-h-sizing #{:fill :fix :auto})
(s/def ::layout-item-v-sizing #{:fill :fix :auto})
(s/def ::layout-item-align-self #{:start :end :center :stretch})
(s/def ::layout-item-max-h ::us/safe-number)
(s/def ::layout-item-min-h ::us/safe-number)
(s/def ::layout-item-max-w ::us/safe-number)
(s/def ::layout-item-min-w ::us/safe-number)
(s/def ::layout-item-absolute boolean?)
(s/def ::layout-item-z-index ::us/safe-integer)

(s/def ::layout-child-props
  (s/keys :opt-un [::layout-item-margin
                   ::layout-item-margin-type
                   ::layout-item-h-sizing
                   ::layout-item-v-sizing
                   ::layout-item-max-h
                   ::layout-item-min-h
                   ::layout-item-max-w
                   ::layout-item-min-w
                   ::layout-item-align-self
                   ::layout-item-absolute
                   ::layout-item-z-index]))

(defn flex-layout?
  ([objects id]
   (flex-layout? (get objects id)))
  ([shape]
   (and (= :frame (:type shape))
        (= :flex (:layout shape)))))

(defn grid-layout?
  ([objects id]
   (grid-layout? (get objects id)))
  ([shape]
   (and (= :frame (:type shape))
        (= :grid (:layout shape)))))

(defn any-layout?
  ([objects id]
   (any-layout? (get objects id)))

  ([shape]
   (or (flex-layout? shape) (grid-layout? shape))))

(defn flex-layout-immediate-child? [objects shape]
  (let [parent-id (:parent-id shape)
        parent (get objects parent-id)]
    (flex-layout? parent)))

(defn any-layout-immediate-child? [objects shape]
  (let [parent-id (:parent-id shape)
        parent (get objects parent-id)]
    (any-layout? parent)))

(defn flex-layout-immediate-child-id? [objects id]
  (let [parent-id (dm/get-in objects [id :parent-id])
        parent (get objects parent-id)]
    (flex-layout? parent)))

(defn any-layout-immediate-child-id? [objects id]
  (let [parent-id (dm/get-in objects [id :parent-id])
        parent (get objects parent-id)]
    (any-layout? parent)))

(defn flex-layout-descent? [objects shape]
  (let [frame-id (:frame-id shape)
        frame (get objects frame-id)]
    (flex-layout? frame)))

(defn grid-layout-descent? [objects shape]
  (let [frame-id (:frame-id shape)
        frame (get objects frame-id)]
    (grid-layout? frame)))

(defn any-layout-descent? [objects shape]
  (let [frame-id (:frame-id shape)
        frame (get objects frame-id)]
    (any-layout? frame)))

(defn inside-layout?
  "Check if the shape is inside a layout"
  [objects shape]

  (loop [current-id (:id shape)]
    (let [current (get objects current-id)]
      (cond
        (or (nil? current) (= current-id (:parent-id current)))
        false

        (= :frame (:type current))
        (:layout current)

        :else
        (recur (:parent-id current))))))

(defn wrap? [{:keys [layout-wrap-type]}]
  (= layout-wrap-type :wrap))

(defn fill-width?
  ([objects id]
   (= :fill (dm/get-in objects [id :layout-item-h-sizing])))
  ([child]
   (= :fill (:layout-item-h-sizing child))))

(defn fill-height?
  ([objects id]
   (= :fill (dm/get-in objects [id :layout-item-v-sizing])))
  ([child]
   (= :fill (:layout-item-v-sizing child))))

(defn auto-width?
  ([objects id]
   (= :auto (dm/get-in objects [id :layout-item-h-sizing])))
  ([child]
   (= :auto (:layout-item-h-sizing child))))

(defn auto-height?
  ([objects id]
   (= :auto (dm/get-in objects [id :layout-item-v-sizing])))
  ([child]
   (= :auto (:layout-item-v-sizing child))))

(defn col?
  ([objects id]
   (col? (get objects id)))
  ([{:keys [layout-flex-dir]}]
   (or (= :column layout-flex-dir) (= :column-reverse layout-flex-dir))))

(defn row?
  ([objects id]
   (row? (get objects id)))
  ([{:keys [layout-flex-dir]}]
   (or (= :row layout-flex-dir) (= :row-reverse layout-flex-dir))))

(defn gaps
  [{:keys [layout-gap]}]
  (let [layout-gap-row (or (-> layout-gap :row-gap) 0)
        layout-gap-col (or (-> layout-gap :column-gap) 0)]
    [layout-gap-row layout-gap-col]))

(defn child-min-width
  [child]
  (if (and (fill-width? child)
           (some? (:layout-item-min-w child)))
    (max 0 (:layout-item-min-w child))
    0))

(defn child-max-width
  [child]
  (if (and (fill-width? child)
           (some? (:layout-item-max-w child)))
    (max 0 (:layout-item-max-w child))
    ##Inf))

(defn child-min-height
  [child]
  (if (and (fill-height? child)
           (some? (:layout-item-min-h child)))
    (max 0 (:layout-item-min-h child))
    0))

(defn child-max-height
  [child]
  (if (and (fill-height? child)
           (some? (:layout-item-max-h child)))
    (max 0 (:layout-item-max-h child))
    ##Inf))

(defn child-margins
  [{{:keys [m1 m2 m3 m4]} :layout-item-margin :keys [layout-item-margin-type]}]
  (let [m1 (or m1 0)
        m2 (or m2 0)
        m3 (or m3 0)
        m4 (or m4 0)]
    (if (= layout-item-margin-type :multiple)
      [m1 m2 m3 m4]
      [m1 m2 m1 m2])))

(defn child-height-margin
  [child]
  (let [[top _ bottom _] (child-margins child)]
    (+ top bottom)))

(defn child-width-margin
  [child]
  (let [[_ right _ left] (child-margins child)]
    (+ right left)))

(defn h-start?
  [{:keys [layout-align-items layout-justify-content] :as shape}]
  (or (and (col? shape)
           (= layout-align-items :start))
      (and (row? shape)
           (= layout-justify-content :start))))

(defn h-center?
  [{:keys [layout-align-items layout-justify-content] :as shape}]
  (or (and (col? shape)
           (= layout-align-items :center))
      (and (row? shape)
           (= layout-justify-content :center))))

(defn h-end?
  [{:keys [layout-align-items layout-justify-content] :as shape}]
  (or (and (col? shape)
           (= layout-align-items :end))
      (and (row? shape)
           (= layout-justify-content :end))))

(defn v-start?
  [{:keys [layout-align-items layout-justify-content] :as shape}]
  (or (and (row? shape)
           (= layout-align-items :start))
      (and (col? shape)
           (= layout-justify-content :start))))

(defn v-center?
  [{:keys [layout-align-items layout-justify-content] :as shape}]
  (or (and (row? shape)
           (= layout-align-items :center))
      (and (col? shape)
           (= layout-justify-content :center))))

(defn v-end?
  [{:keys [layout-align-items layout-justify-content] :as shape}]
  (or (and (row? shape)
           (= layout-align-items :end))
      (and (col? shape)
           (= layout-justify-content :end))))

(defn content-start?
  [{:keys [layout-align-content]}]
  (= :start layout-align-content))

(defn content-center?
  [{:keys [layout-align-content]}]
  (= :center layout-align-content))

(defn content-end?
  [{:keys [layout-align-content]}]
  (= :end layout-align-content))

(defn content-between?
  [{:keys [layout-align-content]}]
  (= :space-between layout-align-content))

(defn content-around?
  [{:keys [layout-align-content]}]
  (= :space-around layout-align-content))

(defn content-evenly?
  [{:keys [layout-align-content]}]
  (= :space-evenly layout-align-content))

(defn content-stretch?
  [{:keys [layout-align-content]}]
  (or (= :stretch layout-align-content)
      (nil? layout-align-content)))

(defn align-items-center?
  [{:keys [layout-align-items]}]
  (= layout-align-items :center))

(defn align-items-start?
  [{:keys [layout-align-items]}]
  (= layout-align-items :start))

(defn align-items-end?
  [{:keys [layout-align-items]}]
  (= layout-align-items :end))

(defn align-items-stretch?
  [{:keys [layout-align-items]}]
  (= layout-align-items :stretch))

(defn reverse?
  [{:keys [layout-flex-dir]}]
  (or (= :row-reverse layout-flex-dir)
      (= :column-reverse layout-flex-dir)))

(defn space-between?
  [{:keys [layout-justify-content]}]
  (= layout-justify-content :space-between))

(defn space-around?
  [{:keys [layout-justify-content]}]
  (= layout-justify-content :space-around))

(defn space-evenly?
  [{:keys [layout-justify-content]}]
  (= layout-justify-content :space-evenly))

(defn align-self-start? [{:keys [layout-item-align-self]}]
  (= :start layout-item-align-self))

(defn align-self-end? [{:keys [layout-item-align-self]}]
  (= :end layout-item-align-self))

(defn align-self-center? [{:keys [layout-item-align-self]}]
  (= :center layout-item-align-self))

(defn align-self-stretch? [{:keys [layout-item-align-self]}]
  (= :stretch layout-item-align-self))

(defn layout-absolute?
  ([objects id]
   (layout-absolute? (get objects id)))
  ([shape]
   (true? (:layout-item-absolute shape))))

(defn layout-z-index
  ([objects id]
   (layout-z-index (get objects id)))
  ([shape]
   (or (:layout-item-z-index shape) 0)))

(defn change-h-sizing?
  [frame-id objects children-ids]
  (and (flex-layout? objects frame-id)
       (auto-width? objects frame-id)
       (or (and (col? objects frame-id)
                (->> children-ids
                     (remove (partial layout-absolute? objects))
                     (every? (partial fill-width? objects))))
           (and (row? objects frame-id)
                (->> children-ids
                     (remove (partial layout-absolute? objects))
                     (some (partial fill-width? objects)))))))

(defn change-v-sizing?
  [frame-id objects children-ids]
  (and (flex-layout? objects frame-id)
       (auto-height? objects frame-id)
       (or (and (col? objects frame-id)
                (some (partial fill-height? objects) children-ids))
           (and (row? objects frame-id)
                (every? (partial fill-height? objects) children-ids)))))

(defn remove-layout-container-data
  [shape]
  (dissoc shape
          :layout
          :layout-flex-dir
          :layout-gap
          :layout-gap-type
          :layout-wrap-type
          :layout-padding-type
          :layout-padding
          :layout-justify-content
          :layout-align-items
          :layout-align-content
          :layout-grid-dir
          :layout-justify-items
          :layout-grid-columns
          :layout-grid-rows
          ))

(defn remove-layout-item-data
  [shape]
  (dissoc shape
          :layout-item-margin
          :layout-item-margin-type
          :layout-item-h-sizing
          :layout-item-v-sizing
          :layout-item-max-h
          :layout-item-min-h
          :layout-item-max-w
          :layout-item-min-w
          :layout-item-align-self
          :layout-item-absolute
          :layout-item-z-index))
(declare assign-cells)

(def grid-cell-defaults
  {:row-span 1
   :column-span 1
   :position :auto
   :align-self :auto
   :justify-self :auto
   :shapes []})

;; TODO: GRID ASSIGNMENTS

;; Adding a track creates the cells. We should check the shapes that are not tracked (with default values) and assign to the correct tracked values
(defn add-grid-column
  [parent value]
  (us/assert ::grid-definition value)
  (let [rows (:layout-grid-rows parent)
        new-col-num (count (:layout-grid-columns parent))

        layout-grid-cells
        (->> (d/enumerate rows)
             (reduce (fn [result [row-idx _row]]
                       (let [id (uuid/next)]
                         (assoc result id
                                (merge {:id id
                                        :row (inc row-idx)
                                        :column new-col-num
                                        :track? true}
                                       grid-cell-defaults))))
                     (:layout-grid-cells parent)))]
    (-> parent
        (update :layout-grid-columns (fnil conj []) value)
        (assoc :layout-grid-cells layout-grid-cells))))

(defn add-grid-row
  [parent value]
  (us/assert ::grid-definition value)
  (let [cols (:layout-grid-columns parent)
        new-row-num (inc (count (:layout-grid-rows parent)))

        layout-grid-cells
        (->> (d/enumerate cols)
             (reduce (fn [result [col-idx _col]]
                       (let [id (uuid/next)]
                         (assoc result id
                                (merge {:id id
                                        :column (inc col-idx)
                                        :row new-row-num
                                        :track? true}
                                       grid-cell-defaults))))
                     (:layout-grid-cells parent)))]
    (-> parent
        (update :layout-grid-rows (fnil conj []) value)
        (assoc :layout-grid-cells layout-grid-cells))))

;; TODO: Remove a track and its corresponding cells. We need to reassign the orphaned shapes into not-tracked cells
(defn remove-grid-column
  [parent _index]
  parent)

(defn remove-grid-row
  [parent _index]
  parent)

;; TODO: Mix the cells given as arguments leaving only one. It should move all the shapes in those cells in the direction for the grid
;; and lastly use assign-cells to reassing the orphaned shapes
(defn merge-cells
  [parent _cells]
  parent)


;; TODO
;; Assign cells takes the children and move them into the allotted cells. If there are not enough cells it creates
;; not-tracked rows/columns and put the shapes there
;; Should be caled each time a child can be added like:
;;  - On shape creation
;;  - When moving a child from layers
;;  - Moving from the transform into a cell and there are shapes without cell
;;  - Shape duplication
;;  - (maybe) create group/frames. This case will assigna a cell that had one of its children
(defn assign-cells
  [parent]
  #_(let [allocated-shapes
        (into #{} (mapcat :shapes) (:layout-grid-cells parent))

        no-cell-shapes
        (->> (:shapes parent) (remove allocated-shapes))])
  parent)
