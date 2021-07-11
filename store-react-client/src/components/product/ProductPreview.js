import React, {useEffect, useState} from "react";
import "./style/Product.css";
import {fetchImage, fetchProducts} from "../../api/products";

export function ProductPreview(props) {

    const [image, setImage] = useState([]);

    useEffect(() => {
        fetchImage(props.id)
            .then((img) => {
                setImage(img)
            })
    }, []);

    const imageUrl = image[0] ? image[0].url  : '';
    const imagePath = process.env.PUBLIC_URL + "/assets/images/" + imageUrl;

    return (
        <div className={"preview-product"}>
            <div className={"preview-product-image"}>
                <img src={imagePath} alt={props.name + " photo"} height={400}/>
            </div>
            <div className={"preview-product-name"}>
                {props.name}
            </div>
            <div className={"preview-product-color"}>
                {props.color}
            </div>
            <div className={"preview-product-price"}>
                <span>{props.price} PLN</span>
            </div>
        </div>
    )
}