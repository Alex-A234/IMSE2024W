import { useState, useEffect } from 'react';
import ReturnOrderForm from "./ReturnOrderForm";
import api from "../router/router";

const OrdersListView = ({ customerSSN, setMainView }) => {
    const [orders, setOrders] = useState([]);

    useEffect(() => {
        api.path.getOrders(customerSSN).then(res => {
            console.log(res);
            setOrders(res.data);
        });
    }, [customerSSN])

    const returnOrder = (order) => {
        setMainView(<ReturnOrderForm order={order} customerSSN={customerSSN}/>);
    }

    return (
        <div class="mt-5">
            <div class="flex justify-center">
                <div class="bg-white w-11/12 text-gray-900">
                    {
                        orders.map((order, index) => {
                            return (
                                <div key={order.orderId} class="pt-5 pb-5 border-b text-left px-6 py-2 w-full hover:bg-blue-100 focus:bg-gray-200 transition duration-100">
                                    <div class="flex w-full">
                                        <span class="flex w-4/12"><b>Order Num</b>: {order.orderId}</span>
                                        <span class="flex w-full"><b>Paid with</b>: {order.cardNumber}</span>
                                        <button class="flex bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-full" onClick={() => returnOrder(order)}>
                                            Return
                                        </button>
                                    </div>
                                    <span class="flex w-full"><b>Ordered</b>: {order.date}</span>
                                    <span class="flex w-full"><b>Total Amount</b>: {order.purchaseAmount.toFixed(2)} € </span>
                                    <span class="flex w-full"><b>Products</b>:</span>
                                    {
                                        order.orderProducts.map((product, index) => {
                                            return (
                                                <div key={index} class="flex">
                                                    <span class="flex w-full"><b>Producer</b>: {product.product.producer.producerName}</span>
                                                    <span class="flex w-full"><b>Name</b>: {product.product.productName}</span>
                                                    <span class="flex w-full"> <b>Amount</b>: {product.amount} </span>
                                                    <span class="flex w-full"> <b>Price/Unit</b>: {product.product.pricePerUnit.toFixed(2)} € </span>
                                                </div>
                                            );
                                        })
                                    }
                                </div>
                            )
                        })
                    }
                </div>
            </div>
        </div>
    );
}

export default OrdersListView;