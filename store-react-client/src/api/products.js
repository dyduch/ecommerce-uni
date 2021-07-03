export const fetchProducts = async () => {
    return await fetch(`http://localhost:9000/products`)
        .then(response => response.json());
}
