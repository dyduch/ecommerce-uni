import React, {useContext} from "react";
import ShopContext from "../../contexts/ShopContext";
import {ProductPreview} from "./ProductPreview";
import CartContext from "../../contexts/CartContext";
import "./style/Product.css";

export function Products(props) {
    const {products} = useContext(ShopContext);
    const {addItem} = useContext(CartContext);

    return (
        <div>
            <div className="products">
                <ul className="products-list">
                    {products.map((product) => (
                        <li>
                            <ProductPreview name={product.name} color={product.color} price={product.price} image={product.image}/>
                            <button onClick={() => addItem(product)}>Add To Cart</button>
                        </li>))}
                </ul>
            </div>
        </div>

    )
}
