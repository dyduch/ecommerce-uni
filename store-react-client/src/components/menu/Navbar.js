import React from "react";
import {BrowserRouter, Link, Route} from "react-router-dom";
import {PageHeader} from "../Header";
import {Products} from "../product/Products";
import {ShoppingCart} from "../cart/ShoppingCart";

export function Navbar(props) {
    return (
        <BrowserRouter>
            <ul>
                <li><Link to="/">Home</Link></li>
                <li><Link to="/cart">Shopping Cart</Link></li>
                <li><Link to="/products">Products</Link></li>
            </ul>
            <Route path="/" component={PageHeader}/>
            <Route path="/products" component={Products}/>
            <Route path="/cart" component={ShoppingCart}/>
        </BrowserRouter>
    )
}