import { useState, useEffect } from 'react';
import api from '../router/router';

function classNames(...classes) {
    return classes.filter(Boolean).join(' ')
}


const ReturnOrderForm = ({ customerSSN, order }) => {
    const [returnOrder, setReturnOrder] = useState(new Array(order.orderProducts.length));
    const [description, setDescription] = useState('');
    const [canSubmit, setCanSubmit] = useState(false);
    const [err, setErr] = useState(null);

    useEffect(() => {
        setCanSubmit(false);
        for (let i = 0; i < returnOrder.length; i++) {
            if (returnOrder[i] > 0) {
                setCanSubmit(true);
            }
        }
    }, [returnOrder])

    const increase = (i) => {
        let value = returnOrder[i];
        if (!value) {
            value = 0;
        }

        value += 1;

        if (value > order.orderProducts[i].amount) {
            value = order.orderProducts[i].amount
        }

        let newArray = [...returnOrder];
        newArray[i] = value;

        setReturnOrder(newArray);
    }

    const decrease = (i) => {
        let value = returnOrder[i];
        if (!value) {
            value = 0;
        }

        value -= 1;

        if (value < 0) {
            value = 0;
        }

        let newArray = [...returnOrder];
        newArray[i] = value;

        setReturnOrder(newArray);
    }

    const submitReturnOrder = () => {
        let ro = {
            description: description,
            customer: customerSSN,
            products: []
        };

        for (let i = 0; i < order.orderProducts.length; i++) {
            if (returnOrder[i]) {
                ro.products.push({
                    amount: returnOrder[i],
                    product: order.orderProducts[i].product.productNumber
                })
            }
        }

        if (ro.products.length > 0) {
            api.path.postReturnOrder(order.orderId, ro).then(res => {
                setErr(false);
            }).catch(err => {
                if (err.response.status !== 200) {
                    setErr(true);
                }
            })
        }
    }

    return (
        <div class="mt-5">
            <div class="flex justify-center">
                {err === true ?
                    <div class="bg-red-100 rounded-lg py-5 px-6 mb-4 text-base text-red-700 mb-3">
                        Failed to return order please check that you enetered a valid return amount. Or check
                        if you already returned the selected products.
                    </div>
                    :
                    err === null ?
                        <div></div>
                        :
                        <div class="bg-green-100 rounded-lg py-5 px-6 mb-4 text-base text-green-700 mb-3">
                            Return order processed sucessfuly
                        </div>
                }
            </div>
            <div class="flex justify-center">
                <div class="bg-white w-11/12 text-gray-900">
                    <span class="flex justify-center w-full"> <b>Order Information</b> </span>
                    <div class="flex w-full">
                        <span class="flex w-4/12"><b>Order Num</b>: {order.orderId}</span>
                        <span class="flex w-full"><b>Paid with</b>: {order.cardNumber}</span>
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
                                    <span class="flex w-6/12"> <b>Amount</b>: {product.amount} </span>
                                    <span class="flex w-6/12"> <b>Price/Unit</b>: {product.product.pricePerUnit.toFixed(2)} € </span>

                                    <span class="flex w-6/12"> <b>Return</b>: {returnOrder[index] ? returnOrder[index] : '-'} </span>
                                    <button class="mr-2 mt-0.5 flex w-1/12 bg-blue-500 hover:bg-blue-700 text-white font-bold rounded justify-center" onClick={() => { increase(index) }}>+</button>
                                    <button class="mt-0.5 flex w-1/12 bg-blue-500 hover:bg-blue-700 text-white font-bold rounded justify-center" onClick={() => { decrease(index) }}>-</button>
                                </div>
                            );
                        })
                    }
                </div>
            </div>
            <div class="mt-10 flex justify-center">
                <div class="w-11/12">
                    <textarea
                        class="form-control block w-full px-3 py-1.5 text-base font-normal text-gray-700 bg-white bg-clip-padding border border-solid border-gray-300 rounded transition ease-in-out m-0 focus:text-gray-700 focus:bg-white focus:border-blue-600 focus:outline-none"
                        id="description"
                        rows="3"
                        placeholder="Why you want to return the products?"
                        onChange={(event) => { setDescription(event.target.value) }}
                    ></textarea>
                </div>
            </div>

            <div class="flex justify-center">
                <div class="flex mt-10 w-11/12 justify-center">
                    <div class="w-1/12">
                        <button class={classNames(canSubmit ? '' : 'cursor-not-allowed', "mt-0.5 flex p-2 bg-blue-500 hover:bg-blue-700 text-white font-bold rounded justify-center")} onClick={() => { submitReturnOrder() }}>Submit</button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ReturnOrderForm;

