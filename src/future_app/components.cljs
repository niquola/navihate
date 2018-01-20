(ns future-app.components
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync] :as rf]
            [route-map.core :as rm]
            [clojure.string :as str]))

(def ReactNative (js/require "react-native"))
(def app-registry (.-AppRegistry ReactNative))

(defn c [prop] (r/adapt-react-class (aget ReactNative prop)))

(def btn (c "Button"))

(defn register [nm cmp]
  (.registerComponent app-registry nm  #(r/reactify-component cmp)))


(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))
(def touchable-opacity (r/adapt-react-class (.-TouchableOpacity ReactNative)))
(def list-view (r/adapt-react-class (.-ListView ReactNative)))

(def styles
  {:text
   {:text-align "center"
    :font-weight "bold"}
   :touch {:background-color "#999" :padding 10 :border-radius 5}

   :blue {:color "red"
          :background-color "#f1f1f1"
          :padding 10
          :font-size 32}

   :main {:background-color "#f1f1f1"
          :padding-vertical 20
          :padding-horizontal 10
          :flex 1
          :flex-direction "column"}})

(defn alert [title]
      (.alert (.-Alert ReactNative) title))

(defn app-text [x]
  [text {:style {:font-family "Helvetica Neue"}} x])

(defn title [x]
  [text {:style {:font-family "HelveticaNeue-CondensedBold"
                 :font-size 18
                 :margin-top 10
                 :margin-bottom 5
                 :color "#573"}} x])

(defn small-text [x]
  [text {:style {:font-family "Helvetica Neue"
                 :font-size 11}} x])
(def blue "#0366d6")

(defn link [href txt]
  [touchable-opacity {:on-press  #(rf/dispatch [:navigate href] href)}
   [text {:style {:color blue
                  :font-size 14
                  :margin-top 4
                  :margin-bottom 4}} txt]])

(defn back []
  [touchable-opacity {:on-press  #(rf/dispatch [:navigate-back]) :style {:padding 8}}
   [text {:style {:color blue :font-size 16}} " ‹‹ "]])

(defn tab [href txt act]
  [touchable-opacity {:on-press  #(rf/dispatch [:navigate href] href)
                      :style {:flex 1
                              :padding 8
                              :margin-right 5}}
   [text {:style {:font-size 16
                  :font-weight (when act "bold")
                  :color (if act "black" "#888")
                  :text-align "center"}} txt]])

(defn animate [op]
  (when-not (> @op 1) 
    (reset! op (+ @op @op))
    (js/setTimeout #(animate op) 10)))


(defn fade-view [_]
  (let [op (r/atom 0.1)]
    ;; (animate op)
    (fn [& cmps]
      ;; :opacity @op 
      (into [view {:style {:flex 1 :padding 10}}]
            cmps))))

(defn top-bar [& cnt]
  (into
   [view {:style {:flex-direction "row"
                  :borderBottomColor "#ddd"
                  :align-items "center"
                  :borderBottomWidth 0.5}}]
   cnt))

(defn url-bar []
  (let [route (subscribe [:route])
        loc (subscribe [:location])]
    (fn []
      [text {:style {:font-size 6
                     :text-align "center"
                     :color "gray"}}
       (str "//" (str/join ":" (:path @loc))  " "
            (when-let [p (:params @loc)] (pr-str p))
            " => " (:match @route))])))

(defn menu [params]
  (fn [{{m :match} :route}]
    (.log js/console m)
    [top-bar
     [back]
     [tab {:path []} "Home" (= m :index)]
     [tab {:path [:chats]} "Chats" (= m :chats)]
     [tab {:path [:record]} "Record" (= m :record)]
     [tab {:path [:profile]} "Profile" (= m :profile)]]))

(defn layout [params cnt]
  [view {:style {:flex 1 :flex-direction "column"}}
   [menu params]
   cnt])
