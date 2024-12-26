import React from 'react'
import { useState, useEffect } from 'react'
import api from '../router/router'

function classNames(...classes) {
  return classes.filter(Boolean).join(' ')
}

const ShoppingCartView = ({ users, currentUser}) => {
  const [products, setProducts] = useState([]);
  const [err, setErr] = useState(null);
  const [orderProducts, setOrderProducts] = useState([]);
  const [sum, setSum] = useState(0);
  const [creditCardNr, setCreditCardNr] = useState('')
  const [name, setName] = useState('')
  const [shipment, setShipment] = useState('')
  const [expirationDate, setExpirationDate] = useState('')
  
  useEffect(() => {
    loadShoppingCartProducts();
    console.log(users)
  }, [])

  useEffect(()=> {
    console.log(products)
    if(orderProducts.length === 0){
      let arr = new Array(products.length)
      arr.fill(1)
      setOrderProducts(arr)
    }
    console.log('inside useEffect - products')
    console.log(products)
  }, [products])


  useEffect(() => {
    console.log('Setting the sum value now!')
    console.log(products)
    let productSum = 0
    for (let index = 0; index < products.length; index++) {
      if(orderProducts[index] === 0){
        continue
      }
      const productPrice = orderProducts[index] * products[index].pricePerUnit
      productSum = productSum + productPrice
    }
    setSum(productSum)
    console.log(productSum) 
  }, [orderProducts])
  

  const increase = (i) => {
    let value = orderProducts[i];
    if (!value) {
        value = 0;
    }

    value += 1;


    let newArray = [...orderProducts];
    newArray[i] = value;

    setOrderProducts(newArray);
}

const decrease = (i) => {
    let value = orderProducts[i];
    if (!value) {
        value = 0;
    }

    value -= 1;

    if (value < 0) {
        value = 0;
    }

    let newArray = [...orderProducts];
    newArray[i] = value;

    setOrderProducts(newArray);
}

const submitOrder=()=>{

  if(creditCardNr.length <= 5){
    setErr(true);
    return;
  }

  if(name.length === 0){
    setErr(true);
    return;
  }

  const customerssn = users[currentUser].ssn

  console.log(shipment)

  let so = {
    shippingType: shipment,
    creditCardNumber: creditCardNr,
    creditCardName: name,
    expirationDate: expirationDate,
    productSum : sum,
    products: []
  }

  console.log(products.length)
  console.log(orderProducts[0])

  for (let i = 0; i < products.length; i++) {
    if (orderProducts[i]) {
        so.products.push({
            amount: orderProducts[i],
            product: products[i].productNumber
        })
    }
  }



  if (so.products.length > 0) {
    api.path.postOrder(customerssn, so).then(res => {
        setErr(false);
        console.log('Order submitted')
    }).catch(err => {
      console.log('something went wrong with network!')
        if (err.response.status !== 200) {
            setErr(true);
        }
    })
  } else {
    setErr(true);
  }

  
}
  
  const loadShoppingCartProducts=()=>{
    console.log('users in SHoppingCartView:')
    console.log(users)
    console.log(currentUser)
    const customerSSN = users[currentUser].ssn
    let helperProductList = []
    api.path.getShoppingCart(customerSSN).then(res => helperProductList.push(res.data))
    setTimeout(function() {
      console.log('inside loadShoppingCartProducts')
      console.log(helperProductList)
      if(helperProductList[0] !== ""){
        setProducts(...products, helperProductList[0])
      }
    }, 50)
  }


  return (
    <div class="mt-5">
                  <div class="flex justify-center">
                {err === true ?
                    <div class="bg-red-100 rounded-lg py-5 px-6 mb-4 text-base text-red-700 mb-3">
                        Failed to submit order.
                    </div>
                    :
                    err === null ?
                        <div></div>
                        :
                        <div class="bg-green-100 rounded-lg py-5 px-6 mb-4 text-base text-green-700 mb-3">
                            Submit order processed sucessfuly
                        </div>
                }
            </div>
    <div class="flex justify-center">
        <div class="bg-white w-11/12 text-gray-900">
          <div>
            <span class="font-bold text-3xl flex w-full">Submit an order</span>
            <span class="my-4 font-bold text-xl flex w-full">
              <b>Total price </b>: {sum.toFixed(2)}
            </span>
            <label for="shipmenttypes" class="block mb-2 text-sm font-medium text-gray-900 dark:text-gray-400">Select an option</label>
            <select id="shipmenttypes" class="mb-4 bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500" onChange={(event) => {
              setShipment(event.target.value) 
            }}>
            <option value="normal">NORMAL</option>
            <option value="express">EXPRESS</option>
            </select>
            <form class="px-1 rounded max-w-3xl w-full my-4 inputs space-y-6">
              <div>
                <p class="text-gray-900">
                  Payment Information:
                </p>
              </div>
              <div class="flex space-x-4">
                <div class="w-1/2">
                  <label for="firstname">Name:</label>
                  <input
                    class="border border-gray-400 px-4 py-2 rounded w-full focus:outline-none focus:border-teal-400"
                    type="text"
                    name="name"
                    id="name"
                    onChange={(event) => { setName(event.target.value) }}
                  />
                </div>
                <div class="w-1/2">
                  <label for="lastname">Expiration Date: </label>
                  <input
                    class="border border-gray-400 px-4 py-2 rounded w-full focus:outline-none focus:border-teal-400"
                    type="text"
                    name="expirationdate"
                    id="expirationdate"
                    onChange={(event) => { setExpirationDate(event.target.value) }}
                    placeholder={'YYYY-MM-DD'}
                  />
                </div>
              </div>
              <div>
                <label for="address">Credit card number:</label>
                <input
                  class="border border-gray-400 px-4 py-2 rounded w-full focus:outline-none focus:border-teal-400"
                  type="text"
                  name="creditcardnr"
                  id="creditcardnr"
                  onChange={(event) => { setCreditCardNr(event.target.value) }}
                />
              </div>
            </form>
              
            <div class="flex justify-center">
                <div class="flex mt-10 w-11/12 justify-center">
                    <div class="w-1/12">
                        <button class={classNames("mt-0.5 flex p-2 bg-blue-500 hover:bg-blue-700 text-white font-bold rounded justify-center")} onClick={() => { submitOrder() }}>Submit</button>
                    </div>
                </div>
            </div>

            <span class="font-bold text-xl">Your shopping cart:</span>
          </div>
            {
                products.map((product, index) => {
                    return (
                        <div key={index} class="pt-5 pb-5 border-b text-left px-6 py-2 w-full hover:bg-blue-100 focus:bg-gray-200 transition duration-100">
                            <span class="flex w-full"><b>Product Num </b>: {product.productNumber}</span>
                            <span class="flex w-full"><b>Price/Unit</b>: {product.pricePerUnit.toFixed(2)} €</span>
                            <span class="flex w-full"><b>Product Name</b>: {product.productName} </span>
                            <span class="flex w-6/12"> <b>Amount:</b>: {orderProducts[index]} </span>
                              <button class="mr-2 mt-0.5 flex w-1/12 bg-blue-500 hover:bg-blue-700 text-white font-bold rounded justify-center" onClick={() => { increase(index) }}>+</button>
                              <button class="mt-0.5 flex w-1/12 bg-blue-500 hover:bg-blue-700 text-white font-bold rounded justify-center" onClick={() => { decrease(index) }}>-</button>
                        </div>
                    )
                })
            }
        </div>
      </div>
    </div>
  )
}

export default ShoppingCartView;