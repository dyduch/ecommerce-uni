import React, {useContext, useState} from "react";
import CartContext from "../../contexts/CartContext";
import {CartProduct} from "./CartProduct";
import {BrowserRouter, Link, Route} from "react-router-dom";
import {PageHeader} from "../Header";
import {Products} from "../product/Products";
import {Checkout} from "../checkout/Checkout";

export function ShoppingCart(props) {
    const {cartItems, totalPrice} = useContext(CartContext);

    return (
        <div>
            <BrowserRouter>
                <button><Link to="/checkout">Checkout</Link></button>
                <Route path="/checkout" component={Checkout}/>

            <div className="cart">
                <div className="total-price">
                    <span>Total price: {totalPrice} PLN</span>
                </div>

                <ul>
                    {cartItems.map((item) => (
                        <li className="cart-item">
                            <CartProduct id={item.id} name={item.name} image={item.image} quantity={item.quantity} price={item.price}/>
                        </li>))}
                </ul>

            </div>
            </BrowserRouter>
        </div>

    )
}
