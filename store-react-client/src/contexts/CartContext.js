import React, { useState } from "react";

const defaultValue = {
    cartItems: [],
    totalPrice: 0,
}

export const CartContext = React.createContext(defaultValue);

export function CartContextProvider(props) {
    const [cartItems, setCartItems] = useState([]);
    const [totalPrice, setTotalPrice] = useState(Number(0));

    const addItem = item => {
        const existingItem = getItem(item.id, cartItems);
        if (existingItem) {
            cartItems[cartItems.findIndex(elem => elem.id === item.id)].quantity += 1;
        } else {
            const newItem = item;
            newItem.quantity = 1;
            setCartItems([
                ...cartItems,
                newItem,
            ]);
        }
        setTotalPrice(price => Number(price) + Number(item.price));
    }

    const removeItem = id => {
        const existingItem = getItem(id, cartItems);
        if (existingItem.quantity > 1) {
            cartItems[cartItems.findIndex(elem => elem.id === id)].quantity -= 1;
        } else {
            const filteredItems = cartItems.filter(product => product.id !== id);
            setCartItems([...filteredItems]);
        }
        setTotalPrice(price => Number(price) - Number(existingItem.price));
    }

    const providerValue = {
        cartItems,
        totalPrice,
        addItem,
        removeItem,
    }

    return (
        <CartContext.Provider value={providerValue}>{props.children}</CartContext.Provider>
    );
}

function getItem(id, cartItems) {
    return cartItems.find(product => product.id === id);
}

export default CartContext;
