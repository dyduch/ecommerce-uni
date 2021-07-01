export function ProductView(props) {
    return (
        <div>
            <div className={"product-images"}>
                <img src={process.env.PUBLIC_URL + props.image} alt={props.name + " photo"} height={400}/>
            </div>
            <div className={"product-name"}>
                {props.name}
            </div>
            <div className={"product-color"}>
                {props.color}
            </div>
            <div className={"product-price"}>
                <span>{props.price} {props.currency}</span>
            </div>
            <div className={"product-sizes"}>

            </div>
            <div className={"product-description"}>

            </div>
            <div className={"buy product"}>

            </div>

        </div>
    )
}