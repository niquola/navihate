(ns future-app.model
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :as rf]
            [route-map.core :as rm]
            [clojure.string :as str]))

(def routes
  {:. :index
   "signin" {:. :signin}
   "record" {:. :record}
   "profile" {:. :profile}
   "chats" {:. :chats
            [:chat-id] {:. :chat}}})

(rf/reg-event-db
 ::initialize-db
 (fn [db _]
   (assoc db
          :location (or (:location db) {:path [] :params {}})
          :location-history [])))

(rf/reg-sub
 :location
 (fn [db _] (get db :location)))

(rf/reg-sub
 :route
 (fn [db _] (get db :route)))

(rf/reg-event-fx
 :navigate-back
 (fn [{db :db} _]
   {:dispatch [:navigate (last (:location-history db)) true]
    :db (update db :location-history (fn [x] (into [] (butlast x))))}))

(rf/reg-event-db
 :navigate
 (fn [db [_ loc hx]]
   (let [m (or (rm/match (str/join "/" (mapv name (:path loc))) routes)
               {:match :not-found :params {}})]
     (cond-> (assoc db
                    :location (merge {:params {}} loc)
                    :route {:match (:match m)
                            :params (:params m)})
       (not hx) (assoc :location-history (conj (:location-history db) (:location db)))))))
