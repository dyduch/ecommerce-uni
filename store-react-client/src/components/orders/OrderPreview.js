import React from "react";
import {OrderStatus} from "./OrderStatus";
import {OrderItems} from "./OrderItems";

export function OrderPreview(props) {

    const {id, date, total} = props.order;

    return (
        <div className={"preview-order"}>
            <div className={"preview-order-date"}>
                Order Date: {date}
            </div>
            <OrderStatus id={id} />
            <div className={"preview-order-price"}>
                Total price: {total} PLN
            </div>
            <OrderItems id={id}/>
        </div>
    )
}