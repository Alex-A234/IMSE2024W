import React from 'react'
import { useState } from 'react';
import { useEffect } from 'react';
import api from '../router/router';

export default function Products({ users, customerSSN, setMainView }) {

  const [products, setProducts] = useState([]);
  const [err, setErr] = useState(null);

  useEffect(() => {
    setProductList(users);
  }, [users])


  const setProductList = (users) => {
    const userList =  users.filter(user => !user.isCustomer)
    let productArrayOfArrays = []
    userList.forEach(element => {
      api.path.getProducts(element.ssn).then((res)=>{
        productArrayOfArrays.push(res.data)
      })
    });
    const productList = []
    setTimeout(function() {
      productArrayOfArrays.forEach(arr => {
        arr.forEach((elem) => {
          productList.push(elem)
        }) 
      })
      setProducts(...products, productList)
    }, 100)
  }


  const addToShoppingCart = (product) => {
    console.log(product)
    const helperProductList = []
    api.path.getShoppingCart(customerSSN).then(res => helperProductList.push(res.data))
    console.log('shopping cart is loaded')
    setTimeout(function() {
      let addedProduct = {
        product: product
      }
      if(helperProductList[0] === ""){
        api.path.postAddToShoppingCart(customerSSN, addedProduct).then(res => {
          setErr(false);
        }).catch(err => {
          if (err.response.status !== 200) {
              console.log('network error in adding product')
              setErr(true);
          }
        })
      } else {
        const productList = helperProductList[0].slice()
        let productIsInShoppingCart = false
        productList.forEach(prod => {
          if(prod.productNumber === product.productNumber) productIsInShoppingCart = true;
        })
        if(!productIsInShoppingCart){
          api.path.postAddToShoppingCart(customerSSN, addedProduct).then(res => {
            setErr(false);
          }).catch(err => {
            if (err.response.status !== 200) {
                console.log('network error in adding product')
                setErr(true);
            }
          })
        }
        console.log('Shopping cart is not empty!')
        setErr(true);
      }
      }, 50)
  }
  
  return (
    <div class="mt-5">
      <div class="flex justify-center">
        {err === true ?
                    <div class="bg-red-100 rounded-lg py-5 px-6 mb-4 text-base text-red-700 mb-3">
                        Product can not be added. Possible problems: Product is already in shopping cart or database issue
                    </div>
                    :
                    err === null ?
                        <div></div>
                        :
                        <div class="bg-green-100 rounded-lg py-5 px-6 mb-4 text-base text-green-700 mb-3">
                            Product added sucessfully
                        </div>
                }
            </div>
        <div class="flex justify-center">
            <div class="bg-white w-11/12 text-gray-900">
                {
                    products.map((product) => {
                        return (
                            <div key={product.productNumber} class="pt-5 pb-5 border-b text-left px-6 py-2 w-full hover:bg-blue-100 focus:bg-gray-200 transition duration-100">
                                <div class="flex w-full"> 
                                <span class="flex w-full"><b>Product Num </b>: {product.productNumber}</span>
                                  <button class="flex bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-full" onClick={() => addToShoppingCart(product)} >
                                          Add
                                  </button>
                                </div>
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