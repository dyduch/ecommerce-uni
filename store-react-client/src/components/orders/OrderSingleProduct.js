import {fetchOrderItems} from "../../api/orders";
import React, {useEffect, useState} from "react";
import {fetchProduct} from "../../api/products";

export function OrderSingleProduct(props) {

    const [product, setProduct] = useState({});

    useEffect(() => {
        fetchProduct(props.product_id)
            .then((i) => {
                setProduct(i)
            })
    }, []);

    return (
        <span> Name: {product.name} | Quantity: {props.quantity} </span>
    )
}