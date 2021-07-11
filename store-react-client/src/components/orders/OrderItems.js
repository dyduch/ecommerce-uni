import {fetchOrderItems} from "../../api/orders";
import React, {useEffect, useState} from "react";
import {OrderSingleProduct} from "./OrderSingleProduct";

export function OrderItems(props) {

    const [items, setItems] = useState([]);

    useEffect(() => {
        fetchOrderItems(props.id)
            .then((i) => {
                setItems(i)
            })
    }, []);

    return (
        <div className={"preview-order-products"}>
            Products:
            <ol>
                {items.map((i) => (
                    <li>
                        <OrderSingleProduct product_id={i.product_id} quantity={i.quantity} />
                    </li>))}
            </ol>
        </div>
    )
}