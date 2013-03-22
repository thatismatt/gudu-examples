(ns gudu-examples.blog
  (:require [gudu])
  (:use [gudu.middleware]
        [ring.util.response]
        [hiccup.core]
        [hiccup.page]
        [hiccup.element]))

(def routes
  {:home []
   :blog ["blog"
          {:latest []
           :post   [gudu/string-segment]
           ;; TODO
           ;;:archive [gudu/integer-segment  ;; year
           ;;          gudu/integer-segment] ;; month
           }]})

(def gu (gudu/gu routes))

(def posts
  {"first-post"   {:title "My First Post"
                   :body "This is my first post..."}
   "next-post"    {:title "The next one"
                   :body "What more could you want from a post"}
   "another-post" {:title "Yet another amazing post"
                   :body "This one is the most enlightening."}})

(defn page [title body]
  (html
   (html5
    [:html
     [:head
      [:title "gudu examples"]
      [:style (clojure.string/join
               " "
               ["ul { display: block; overflow: hidden; padding: 0; }"
                "li { float: left; margin: 0 5px; list-style: none; }"
                ".wrapper { margin: 0 auto; width: 500px; }"])]]
     [:body
      [:div.wrapper
       [:h1 "gudu examples"]
       [:ul
        [:li (link-to (gu :home) "Home")]
        [:li (link-to (gu :blog :latest) "Latest Posts")]]
       (if title [:h2 title])
       [:div body]]]])))

(defn ok-html [body]
  (-> (response body)
      (content-type "text/html")))

(def ok-page (comp ok-html page))

(defn home [req]
  (ok-page "Welcome to this gudu example" "This is my blog."))

(defn latest-posts [req]
  (ok-page
   "Welcome to my blog"
   (map (fn [[slug post]] [:div (link-to (gu :blog :post slug) (post :title))]) posts)))

(defn blog-archive [slug]
  (fn [req]
    (if-let [post (posts slug)]
      (ok-page (post :title) (post :body)))))

(defn missing [req]
  (-> (page "Not Found" "That page is missing.")
      not-found
      (content-type "text/html")))

(defn get-handler [route]
  (let [[ps [arg]] (split-at 2 route)]
    (get-in
     {:home home
      :blog {:latest latest-posts
             :post   (blog-archive arg)}}
     ps)))

(def app
  (-> (router get-handler routes)
      (wrap-route routes)
      ((fn [h] (fn [req] (or (h req) (missing req)))))))
