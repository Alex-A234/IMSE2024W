import { useState, useEffect } from 'react';
import api from "../router/router";

const ProductsListView = ({ producerSSN }) => {
    const [products, setProducts] = useState([]);

    useEffect(() => {
        api.path.getProducts(producerSSN).then(res => {
            setProducts(res.data);
        })
    }, [producerSSN])

    return (
        <div class="mt-5">
            <div class="flex justify-center">
                <div class="bg-white w-11/12 text-gray-900">
                    {
                        products.map((product, index) => {
                            return (
                                <div key={product.productNumber} class="pt-5 pb-5 border-b text-left px-6 py-2 w-full hover:bg-blue-100 focus:bg-gray-200 transition duration-100">
                                    <span class="flex w-full"><b>Product Num: </b>: {product.productNumber}</span>
                                    <span class="flex w-full"><b>Price/Unit</b>: {product.pricePerUnit.toFixed(2)} €</span>
                                    <span class="flex w-full"><b>Amount</b>: {product.amount}</span>
                                    <span class="flex w-full"><b>Product Name</b>: {product.productName} </span>
                                </div>
                            )
                        })
                    }
                </div>
            </div>
        </div>
    );
}

export default ProductsListView;