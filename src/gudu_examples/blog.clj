(ns gudu-examples.blog
  (:require [clojure.string :as str]
            [gudu.core :as gd]
            [gudu.middleware :as gdm]
            [gudu.segment :as gds]
            [ring.util.response :as response]
            [hiccup.core :as h]
            [hiccup.page :as hp]
            [hiccup.element :as he]))

(def routes
  {:home gds/root
   :blog ["blog"
          {:latest gds/root
           :post   gds/string
           ;; TODO
           ;;:archive [gudu/integer-segment  ;; year
           ;;          gudu/integer-segment] ;; month
           }]})

(def gu (gd/gu routes))

(def posts
  {"first-post"   {:title "My First Post"
                   :body "This is my first post..."}
   "next-post"    {:title "The next one"
                   :body "What more could you want from a post"}
   "another-post" {:title "Yet another amazing post"
                   :body "This one is the most enlightening."}})

(defn page [title body]
  (h/html
   (hp/html5
    [:html
     [:head
      [:title "gudu examples"]
      [:style (str/join
               " "
               ["ul { display: block; overflow: hidden; padding: 0; }"
                "li { float: left; margin: 0 5px; list-style: none; }"
                ".wrapper { margin: 0 auto; width: 500px; }"])]]
     [:body
      [:div.wrapper
       [:h1 "gudu examples"]
       [:ul
        [:li (he/link-to (gu :home) "Home")]
        [:li (he/link-to (gu :blog :latest) "Latest Posts")]]
       (when title [:h2 title])
       [:div body]]]])))

(defn ok-html [body]
  (-> (response/response body)
      (response/content-type "text/html")))

(def ok-page (comp ok-html page))

(defn home [_req]
  (ok-page "Welcome to this gudu example" "This is my blog."))

(defn latest-posts [_req]
  (ok-page
   "Welcome to my blog"
   (map (fn [[slug post]] [:div (he/link-to (gu :blog :post slug) (post :title))]) posts)))

(defn blog-archive [slug]
  (fn [_req]
    (when-let [post (posts slug)]
      (ok-page (post :title) (post :body)))))

(defn missing [_req]
  (-> (page "Not Found" "That page is missing.")
      response/not-found
      (response/content-type "text/html")))

(defn get-handler [route]
  (let [[ps [arg]] (split-at 2 route)]
    (get-in
     {:home home
      :blog {:latest latest-posts
             :post   (blog-archive arg)}}
     ps)))

(def app
  (-> (gdm/router get-handler missing)
      (gdm/wrap-route routes)))
