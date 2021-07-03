import './App.css';

import {BrowserRouter, Link, Route} from 'react-router-dom';

import {ShopContextProvider} from "./contexts/ShopContext";
import {Products} from "./components/product/Products";
import {PageHeader} from "./components/Header";
import React from "react";
import {ShoppingCart} from "./components/cart/ShoppingCart";
import {CartContextProvider} from "./contexts/CartContext";
import {Navbar} from "./components/menu/Navbar";

function App() {
    return (
        <div className="App">
            <div>
                <ShopContextProvider>
                    <CartContextProvider>
                        <Navbar/>
                    </CartContextProvider>
                </ShopContextProvider>
            </div>
        </div>
    );
}

export default App;