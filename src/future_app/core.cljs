(ns future-app.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync] :as rf]
            [route-map.core :as rm]
            [future-app.components :as c]
            [future-app.model :as model]
            [clojure.string :as str]))


(defn index [params]
  [c/layout params
   [c/fade-view
    [c/title "Recent chats"]
    [c/link {:path [:chats "ch1"]} "Chat with Mom"]
    [c/link {:path [:chats "ch2"]} "News by frends"]]])

(defn not-found [params]
  (fn []
    [c/layout params
     [c/fade-view
      [c/text "Not found " (pr-str params)]
      [c/link {:path []} "home"]]]))

(defn signin [opts]
  [c/fade-view
   [c/title "Sign In"]
   [c/text "Your creds"]
   [c/link {:path []} "Sign In"]])


(defn chats [params]
  [c/layout params
   [c/fade-view
    [c/title "Chats"]
    [c/link {:path [:chats "a"]} "With frends"]
    [c/link {:path [:chats "b"]} "With mom"]
    [c/link {:path [:chats "c"]} "With dad"]]])

(defn chat [params]
  [c/view {}
   [c/top-bar [c/back]
    [c/link {:path []} "Home / "]
    [c/link {:path [:chats]} "Chats / "]
    [c/link {:path [:chats (get-in params [:params :chat-id])]}
     (str "#" (get-in params [:params :chat-id]))]]
   [c/fade-view
    [c/title (str "#" (pr-str (:params params)))]
    [c/text "some text"]
    [c/link {:path [:profile]} "Go to profile"]]])

(defn profile [params]
  [c/layout params
   [c/fade-view
    [c/title "Profile"]
    [c/text "Go back"]
    [c/back]
    [c/text "Go to chat"]
    [c/link {:path [:chats "profile-chat"]} "Some specific one"]
    [c/link {:path [:signin]} "Sign Out"]]])

(defn record [params]
  [c/layout params
   [c/title "Record"]])

(def pages
  {nil index
   :index index
   :signin signin
   :chats chats
   :chat chat
   :profile profile
   :record record
   :not-found not-found})

(defn app-root []
  (let [route (rf/subscribe [:route])
        loc (rf/subscribe [:location])]
    (fn []
      (let [cmp (get pages (:match @route))]
        [c/view {:style {:background-color "#f1f1f1"
                         :padding-vertical 20
                         :padding-horizontal 10
                         :flex 1
                         :flex-direction "column"}
                 :on-magic-tap (.log js/console "magic")
                 :on-layout (.log js/console "layout")}
         [c/url-bar]
         (if cmp
           [cmp {:params (:params @route)
                 :route @route}]
           [c/text "Page not found for " (pr-str (:match @route))])]))))


(defn init []
  (dispatch-sync [::model/initialize-db])
  (c/register "FutureApp" app-root))
