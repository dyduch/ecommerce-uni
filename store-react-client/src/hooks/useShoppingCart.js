import {useContext, useState} from "react";
import CartContext from "../contexts/CartContext";

export function useShoppingCart() {
    const [cart, setCart] = useState([]);
    const {addItem, removeItem, cartItems} = useContext(CartContext);
    // setCart(cartItems);

    function addProduct(product) {
        setCart([
            ...cart,
            product
        ])
        addItem(product);
    }

    function removeProduct(id) {
        const filteredProducts = cart.filter(product => product.id !== id)
        setCart([...filteredProducts])
        removeItem(id);
    }

    return {
        cart,
        addProduct,
        removeProduct,
    }
}
