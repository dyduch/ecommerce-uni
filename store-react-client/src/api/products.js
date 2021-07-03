export const fetchProducts = async () => {
    return await fetch(`http://localhost:9000/products`)
        .then(response => response.json());
}

export const fetchMockProducts = async () => {
    return [
        {
            id: 1,
            image: "adi-bus-vulc-2.jpg",
            name: "Adidas Busenitz II Vulc",
            color: "green/red/white",
            price: "300"
        },

        {
            id: 2,
            image: "adi-3st.jpg",
            name: "Adidas 3ST.004",
            color: "red/black/white",
            price: "400"
        },

        {
            id: 3,
            image: "adi-tyshawn.jpg",
            name: "Adidas Tyshawn",
            color: "black/white/gum",
            price: "350"
        }
    ];
}