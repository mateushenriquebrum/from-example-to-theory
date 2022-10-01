(ns from-example-to-theory.core)

(defn dollar [amount] {:currency :$ :amount amount})

(defn euro [amount] {:currency :€ :amount amount})

(defn multiply [factor another]
  (let [{:keys [currency, amount]} another]
    {:currency currency :amount (* amount factor)}))

(defn divide [divisor another]
  (let [{:keys [currency, amount]} another]
    {:currency currency :amount (/ amount divisor)}))

(defn exchange [rates money to-currency]
  (let [to-rate (to-currency rates)
        from-rate ((:currency money)rates)
        rate (/ to-rate from-rate)
        exchanged (* (:amount money) rate)]
    {:currency to-currency :amount exchanged}))
