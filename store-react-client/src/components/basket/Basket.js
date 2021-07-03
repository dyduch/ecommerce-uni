import React, {useContext} from "react";
import ShopContext from "../contexts/ShopContext";
import useBasket from "../hooks/useBasket";

export function Basket(props) {
    const { products } = useContext(ShopContext)
    const { basket, addProduct, removeProduct } = useBasket();

    return (
        <div>
            <div className="products">
                <ul>
                    {products.map((product) => (<li>
                        {product.name}
                        <button onClick={() => addProduct(product)}>add product to basket</button>
                    </li>))}
                </ul>
            </div>

            <div className="basket">
                <ul>
                    {basket.map((product) => (<li>
                        {product.name}
                        <button onClick={() => removeProduct(product.id)}>remove product</button>
                    </li>))}
                </ul>
            </div>
        </div>

    )
}