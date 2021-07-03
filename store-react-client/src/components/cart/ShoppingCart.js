import React, {useContext} from "react";
import CartContext from "../../contexts/CartContext";
import {CartProduct} from "./CartProduct";

export function ShoppingCart(props) {
    const {cartItems} = useContext(CartContext);

    return (
        <div>
            <div className="cart">
                <ul>
                    {cartItems.map((item) => (
                        <li className="cart-item">
                            <CartProduct id={item.id} name={item.name} image={item.image} quantity={item.quantity} price={item.price}/>
                        </li>))}
                </ul>
            </div>
        </div>

    )
}
