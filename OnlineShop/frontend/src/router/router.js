import axios from "axios";
import { online_shop_url } from "./api";

const getApi = (prefix) => {
    const users = online_shop_url + prefix + "/user";
    const users_report = users + "/report";

    const orders = online_shop_url + prefix + "/order";
    const orders_report = orders + "/report";
    const return_orders = online_shop_url + prefix + "/order/returned";
    const products = online_shop_url + prefix + "/product";
    const shoppingCart = online_shop_url + prefix + "/shoppingCart";

    const fill_database = online_shop_url + "/functions/fillsqldatabase";
    const migrate_database = online_shop_url + "/functions/migratedatabase";

    return {

        getUsers: () => {
            return axios.get(users);
        },

        getUsersReport: () => {
            return axios.get(users_report);
        },

        getOrders: (customerSSN) => {
            return axios.get(orders, {
                headers: {
                    "user-id": customerSSN
                }
            });
        },

        getOrderReport: () => {
            return axios.get(orders_report);
        },

        getReturnOrders: (customerSSN) => {
            return axios.get(return_orders, {
                headers: {
                    "user-id": customerSSN
                }
            });
        },

        getProducts: (producerSSN) => {
            return axios.get(products, {
                headers: {
                    "user-id": producerSSN
                }
            });
        },

        getShoppingCart: (customerSSN) => {
            return axios.get(shoppingCart, {
                headers: {
                    "user-id": customerSSN
                }
            });
        },

        fillDatabase: () => {
            return axios.post(fill_database);
        },

        postAddToShoppingCart: (customerSSN, addedProduct) => {
            return axios.post(shoppingCart + '/' + customerSSN + '/add', addedProduct)
        },

        postOrder: (customerSSN, order) => {
            console.log(order)
            return axios.post(orders + '/' + customerSSN + '/add', order)
        },

        postReturnOrder: (orderId, return_order) => {
            return axios.post(orders + '/' + orderId + '/return', return_order);
        },

        migrateDatabaseToNoSql: () => {
            return axios.post(migrate_database);
        }
    }
}

var api = {
    path: getApi(localStorage.getItem('prefix') ? localStorage.getItem('prefix') : ''),
};

export default api;

export {
    getApi
}