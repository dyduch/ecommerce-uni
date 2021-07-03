import ShopContext from "../../contexts/ShopContext";
import React, {useContext} from "react";
import {OrderPreview} from "./OrderPreview";

export function Orders(props) {
    const {orders} = useContext(ShopContext);

    return (
        <div>
            <div className="orders">
                <ul className="orders-list">
                    {orders.map((order) => (
                        <li>
                            <OrderPreview order={order}/>
                        </li>))}
                </ul>
            </div>
        </div>
    )
}