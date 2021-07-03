export const fetchMockOrders= async () => {
    return [
        {
            id: 1,
            date: "24 Dec 2020",
            status: "New",
            total: "700",
            products: [
                {
                    id: 1,
                    name: "Adidas Busenitz II Vulc",
                    color: "green/red/white",
                    price: "300",
                    quantity: 1
                },

                {
                    id: 2,
                    name: "Adidas 3ST.004",
                    color: "red/black/white",
                    price: "400",
                    quantity: 1
                }
            ]
        },
    ];
}