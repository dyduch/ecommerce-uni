import React from "react";
import "./style/Product.css";

export function ProductPreview(props) {
    const imagePath = process.env.PUBLIC_URL + "/assets/images/" + props.image;
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