import React from "react";
import {BrowserRouter, Link, Route} from "react-router-dom";
import {PageHeader} from "../Header";
import {Products} from "../product/Products";
import {ShoppingCart} from "../cart/ShoppingCart";
import {Orders} from "../orders/Orders";

export function Navbar(props) {
    return (
        <BrowserRouter>
            <ul>
                <li className="nav-item"><Link to="/">Home</Link></li>
                <li className="nav-item"><Link to="/cart">Shopping Cart</Link></li>
                <li className="nav-item"><Link to="/products">Products</Link></li>
                <li className="nav-item"><Link to="/orders">Orders</Link></li>
            </ul>
            <Route path="/" component={PageHeader}/>
            <Route path="/products" component={Products}/>
            <Route path="/cart" component={ShoppingCart}/>
            <Route path="/orders" component={Orders}/>
        </BrowserRouter>
    )
}