(ns from-example-to-theory.core)

(defn make [currency amount] {:currency currency :amount amount})

(defn dollar [amount] (make :$ amount))

(defn euro [amount] (make :€ amount))

(defn multiply [factor another]
  (let [{:keys [currency, amount]} another]
    (make currency (* amount factor))))

(defn divide [divisor another]
  (let [{:keys [currency, amount]} another]
     (make currency (/ amount divisor))))

(defn exchange [rates money to-currency]
  (let [to-rate (to-currency rates)
        from-rate ((:currency money)rates)
        rate (/ to-rate from-rate)
        exchanged (* (:amount money) rate)]
    (make to-currency exchanged)))
