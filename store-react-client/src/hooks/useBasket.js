import {useState} from "react";

export function useBasket() {
    const [basket, setBasket] = useState([]);

    function addProduct(product) {
        setBasket([
            ...basket,
            product
        ])
    }

    function removeProduct(id) {
        const filteredProducts = basket.filter(product => product.id !== id)

        setBasket([...filteredProducts])
    }

    return {
        basket,
        addProduct,
        removeProduct,
    }
}
