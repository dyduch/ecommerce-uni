import React, { useEffect, useState } from "react";
import {fetchMockProducts, fetchProducts} from "../api/products";
import {fetchMockOrders, fetchOrders} from "../api/orders";

const defaultValue = {
    products: [],
    orders: [],
}

export const ShopContext = React.createContext(defaultValue);

export function ShopContextProvider(props) {
    const [products, setProducts] = useState([]);
    const [orders, setOrders] = useState([]);

    const providerValue = {
        products,
        orders,
    }

    useEffect(() => {
        fetchProducts()
            .then((products) => {
                setProducts(products)
            })
    }, []);

    useEffect(() => {
        fetchOrders()
            .then((orders) => {
                setOrders(orders)
            })
    }, []);

    return (
        <ShopContext.Provider value={providerValue}>{props.children}</ShopContext.Provider>
    );
};

export default ShopContext;
