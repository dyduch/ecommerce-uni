import React from "react";
import {ProductPreview} from "../product/ProductPreview";

export function OrderPreview(props) {

    const {date, status, total, products} = props.order;

    return (
        <div className={"preview-order"}>
            <div className={"preview-order-date"}>
                Order Date: {date}
            </div>
            <div className={"preview-order-status"}>
                Status: {status}
            </div>
            <div className={"preview-order-price"}>
                Total price: {total} PLN
            </div>
            <div className={"preview-order-products"}>
                Products:
                <ol>
                    {products.map((product) => (
                        <li>
                            Name: {product.name} | Quantity: {product.quantity}
                        </li>))}
                </ol>
            </div>

        </div>
    )
}