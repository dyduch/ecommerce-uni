import React, { useEffect, useState } from "react";
import {fetchProducts} from "../api/products";

const defaultValue = {
    products: []
}

export const ShopContext = React.createContext(defaultValue);

export function ShopContextProvider(props) {
    const [products, setProducts] = useState([]);

    const providerValue = {
        products,
    }

    useEffect(() => {
        fetchProducts()
            .then((products) => {
                console.log(products)
                setProducts(products)
            })
    }, []);

    return (
        <ShopContext.Provider value={providerValue}>{props.children}</ShopContext.Provider>
    );
};

export default ShopContext;