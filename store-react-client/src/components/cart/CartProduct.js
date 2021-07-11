import React, {useContext, useEffect, useState} from "react";
import "./style/Cart.css";
import CartContext from "../../contexts/CartContext";
import {fetchImage} from "../../api/products";

export function CartProduct(props) {
    const [quantity, setQuantity] = useState(props.quantity);

    const [image, setImage] = useState([]);

    useEffect(() => {
        fetchImage(props.id)
            .then((img) => {
                setImage(img)
            })
    }, []);

    const imageUrl = image[0] ? image[0].url  : '';
    const imagePath = process.env.PUBLIC_URL + "/assets/images/" + imageUrl;

    const {removeItem} = useContext(CartContext);

    return (
        <div className={"cart-product"}>
            <div className={"cart-product-image"}>
                <img src={imagePath} alt={props.name + " photo"} height={400}/>
            </div>
            <div className={"cart-product-name"}>
                {props.name}
            </div>
            <div className={"cart-product-quantity"}>
                {quantity}
            </div>
            <div className={"cart-product-price"}>
                <span>{props.price} PLN</span>
            </div>

            <button onClick={() => {
                setQuantity(quantity - 1);
                removeItem(props.id)
            }}>Remove from cart</button>

        </div>
    )
}
