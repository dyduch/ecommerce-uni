import './App.css';

import {BrowserRouter, Link, Route} from 'react-router-dom';

import {ShopContextProvider} from "./contexts/ShopContext";
import {Products} from "./components/product/Products";
import {PageHeader} from "./components/Header";
import React from "react";
import {ShoppingCart} from "./components/cart/ShoppingCart";
import {CartContextProvider} from "./contexts/CartContext";

function App() {
    return (
        <div className="App">
            <div>
                <ShopContextProvider>
                    <CartContextProvider>
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
                    </CartContextProvider>
                </ShopContextProvider>
            </div>
        </div>
    );
}

export default App;