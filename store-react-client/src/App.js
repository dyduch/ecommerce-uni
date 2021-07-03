import './App.css';

import { BrowserRouter, Link, Route} from 'react-router-dom';

import {ShopContextProvider} from "./contexts/ShopContext";
import {Products} from "./components/product/Products";
import {PageHeader} from "./components/Header";
import React from "react";

function App() {
    return (
        <div className="App">
            <div>
                <ShopContextProvider>
                    <BrowserRouter>
                        <ul>
                            <li><Link to="/">Home</Link></li>
                            <li><Link to="/basket">Basket</Link></li>
                            <li><Link to="/products">Products</Link></li>
                        </ul>
                        <Route path="/" component={PageHeader}/>
                        <Route path="/products" component={Products}/>
                    </BrowserRouter>
                </ShopContextProvider>
            </div>
        </div>
    );
}

export default App;