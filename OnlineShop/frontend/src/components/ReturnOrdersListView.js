import { useState, useEffect } from 'react';
import api from "../router/router";

const OrdersListView = ({ customerSSN }) => {
    const [returnedOrders, setReturnedOrders] = useState([]);

    useEffect(() => {
        api.path.getReturnOrders(customerSSN).then(res => {
            setReturnedOrders(res.data);
        })
    }, [customerSSN])

    return (
        <div class="mt-5">
            <div class="flex justify-center">
                <div class="bg-white w-11/12 text-gray-900">
                    {
                        returnedOrders.map((returnedOrder, index) => {
                            return (
                                <div key={returnedOrder.returnOrderKey.returnOrderId} class="pt-5 pb-5 border-b text-left px-6 py-2 w-full hover:bg-blue-100 focus:bg-gray-200 transition duration-100">
                                    <span class="flex w-full"><b>Return Order Num</b>: {returnedOrder.returnOrderKey.returnOrderId}</span>
                                    <hr />
                                    <span class="flex justify-center w-full"> <b>Order Information</b> </span>
                                    <span class="flex w-full"><b>Order Num</b>: {returnedOrder.order.orderId}</span>
                                    <span class="flex w-full"><b>Ordered</b>: {returnedOrder.order.date}</span>
                                    <span class="flex w-full"><b>Total Amount</b>: {returnedOrder.order.purchaseAmount.toFixed(2)} € </span>
                                    <span class="flex w-full"><b>Products</b>:</span>
                                    {
                                        returnedOrder.order.orderProducts.map((product, index) => {
                                            return (
                                                <div key={index} class="flex">
                                                    <span class="flex w-full"><b>Name</b>: {product.product.productName}</span>
                                                    <span class="flex w-full"> <b>Amount</b>: {product.amount} </span>
                                                    <span class="flex w-full"> <b>Price/Unit</b>: {product.product.pricePerUnit.toFixed(2)} </span>
                                                </div>
                                            );
                                        })
                                    }
                                    <hr />
                                    <span class="flex justify-center w-full"> <b>Return Order Information</b> </span>
                                    <span class="flex w-full"><b>Date</b>: {returnedOrder.date}</span>
                                    <span class="flex w-full"><b>Reason</b>: {returnedOrder.reasonDescription}</span>
                                    <hr />
                                    {
                                        returnedOrder.returnOrderProducts.map((product, index) => {
                                            return (
                                                <div key={product.uuid} class="flex">
                                                    <span class="flex w-full"><b>Name</b>: {product.product.productName}</span>
                                                    <span class="flex w-full"> <b>Amount</b>: {product.amount} </span>
                                                    <span class="flex w-full"> <b>Price/Unit</b>: {product.product.pricePerUnit.toFixed(2)} </span>
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