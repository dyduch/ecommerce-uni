const host = 'http://localhost:9000';

export const fetchOrders = async () => {
    return await fetch(`${host}/api/orders` )
        .then(response => response.json())
}

export const fetchOrderItems = async (order_id) => {
    return await fetch(`${host}/api/orderitems/order/${order_id}` )
        .then(response => response.json())
}

export const fetchOrderStatus = async (order_id) => {
    return await fetch(`${host}/api/orderstatuses/product/${order_id}` )
        .then(response => response.json())
}

export function postOrder(user_id, address_id, total) {
    const d = new Date();
    const monthUpdated = d.getMonth() + 1;
    const month = monthUpdated < 10
        ? "0" + monthUpdated
        : monthUpdated;
    const dateToSend = d.getFullYear() + '-' + month + '-' + d.getDate();

    const params = `date=${dateToSend}&address_id=${address_id}&total=${total}&user_id=${user_id}`

    const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        mode: 'cors',
    };
    console.log(requestOptions)
    return fetch(`${host}/api/orders/add?${params}`, requestOptions);
}

export const fetchMockOrders= async () => {
    return [
        {
            id: 1,
            date: "24 Dec 2020",
            status: "New",
            total: "700",
            products: [
                {
                    id: 1,
                    name: "Adidas Busenitz II Vulc",
                    color: "green/red/white",
                    price: "300",
                    quantity: 1
                },

                {
                    id: 2,
                    name: "Adidas 3ST.004",
                    color: "red/black/white",
                    price: "400",
                    quantity: 1
                }
            ]
        },
    ];
}