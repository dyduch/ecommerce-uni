import React from "react";

export function ProductPreview(props) {
    console.log(props.image)
    return (
        <div>
            <div className={"preview-product-image"}>
                <img src={process.env.PUBLIC_URL + props.image} alt={props.name + " photo"} height={400}/>
            </div>
            <div className={"preview-product-name"}>
                {props.name}
            </div>
            <div className={"preview-product-color"}>
                {props.color}
            </div>
            <div className={"preview-product-price"}>
                <span>{props.price} {props.currency}</span>
            </div>
        </div>
    )
}