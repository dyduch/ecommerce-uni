import {fetchOrderStatus} from "../../api/orders";
import {useEffect, useState} from "react";

export function OrderStatus(props) {

    const [status, setStatus] = useState([]);

    useEffect(() => {
        fetchOrderStatus(props.id)
            .then((status) => {
                setStatus(status)
            })
    }, []);

    const printableStatus = status[0] ? status[0].status : '';

    return (
        <div className={"preview-order"}>
            <div className={"preview-order-status"}>
                Status: {printableStatus}
            </div>
        </div>
    )
}