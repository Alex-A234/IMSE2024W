import { Fragment, useEffect } from 'react'
import { Menu, Transition } from '@headlessui/react'
import { ChevronDownIcon } from '@heroicons/react/solid'
import React, { useState } from 'react';
import UserReportView from './UserReportView';
import OrdersListView from './OrdersListView';
import ProductsListView from './ProductsListView';
import ReturnOrdersListView from './ReturnOrdersListView';
import Products from './Products';
import ShoppingCartView from './ShoppingCartView';
import OrderReportView from './OrderReportView';
import api, { getApi } from '../router/router';

function classNames(...classes) {
    return classes.filter(Boolean).join(' ')
}

const Header = ({ users, currentUser, setCurrentUser, setMainView }) => {
    const [reports] = useState([
        {
            name: 'top 5 returned products',
        },
        {
            name: 'top 10 ordered products'
        }
    ]);

    const [currentMenu, setCurrentMenu] = useState(0);
    const [menu, setMenu] = useState([]);

    const [waitingForFillResult, setWaitingForFillResult] = useState(false);
    const [waitingForMigrateResult, setWaitingForMigrateResult] = useState(false);

    const renderReport = (index) => {
        if (index === 0) {
            api.path.getUsersReport().then(res => {
                setMainView(<UserReportView report={res.data} />);
            })
        } else {
            if (index === 1) {
                console.log('Orderreport gets called')
                api.path.getOrderReport().then(res => {
                    console.log(res.data)
                    setMainView(<OrderReportView report={res.data} />);
                })
            }
        }
    }

    const databaseOperation = (operation) => {
        if (operation === "fill" && waitingForFillResult === false && waitingForMigrateResult === false) {
            setWaitingForFillResult(true);

            api.path.fillDatabase().then(res => {
                setWaitingForFillResult(false);

                window.location.reload();
            });

            return;
        }

        if (operation === "migrate" && waitingForMigrateResult === false && waitingForFillResult === false) {
            setWaitingForMigrateResult(true);

            api.path.migrateDatabaseToNoSql().then(res => {
                setWaitingForMigrateResult(false);

                api.path = getApi('/nosql');
                localStorage.setItem('prefix', '/nosql');

                window.location.reload();
            })

            return;
        }
    }

    useEffect(() => {
        if (users.length > 0) {
            if (users[currentUser].isCustomer) {
                setMenu([
                    {
                        name: 'orders',
                    },
                    {
                        name: 'returned orders',
                    },
                    {
                        name: 'products'
                    }
                ])

                if (currentMenu === 0) {
                    setMainView(<OrdersListView customerSSN={users[currentUser].ssn} setMainView={setMainView} />);
                } else {
                    if (currentMenu === 1) {
                        setMainView(<ReturnOrdersListView customerSSN={users[currentUser].ssn} />);
                    } else {
                        if (currentMenu === 2) {
                            setMainView(<Products users={users} customerSSN={users[currentUser].ssn} setMainView={setMainView} />)
                        }
                    }
                }
            } else {
                setMenu([
                    {
                        name: 'products',
                    }
                ])
                setCurrentMenu(0);

                if (currentMenu === 0) {
                    setMainView(<ProductsListView producerSSN={users[currentUser].ssn} />);
                }
            }
        }
        console.log('users:')
        console.log(users)
    }, [currentUser, currentMenu, setMainView, users])

    const openShoppingCart = (users, currentUser) => {
        setMainView(<ShoppingCartView users={users} currentUser={currentUser} />)
    }

    return (
        <div class="z-50 relative h-full w-full">
            <div class="absolute left-3 top-5 h-10 w-80">
                <div class="absolute inline-flex left-0">
                    {
                        (waitingForFillResult === true)
                            ?
                            <button type="button" class="inline-flex items-center px-4 py-2 font-semibold leading-6 text-sm shadow rounded-md text-white bg-blue-500 hover:bg-blue-400 transition ease-in-out duration-150 cursor-not-allowed" disabled="">
                                <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                </svg>
                                Processing...
                            </button>
                            :
                            <button class="inline-flex bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded" onClick={() => { databaseOperation("fill") }}>
                                Fill database
                            </button>
                    }
                </div>

                <div class="absolute inline-flex right-0">
                    {
                        (waitingForMigrateResult === true)
                            ?
                            <button type="button" class="inline-flex items-center px-4 py-2 font-semibold leading-6 text-sm shadow rounded-md text-white bg-blue-500 hover:bg-blue-400 transition ease-in-out duration-150 cursor-not-allowed" disabled="">
                                <svg class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                </svg>
                                Processing...
                            </button>
                            :
                            <button class="inline-flex bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded" onClick={() => { databaseOperation("migrate") }}>
                                Migrate database
                            </button>
                    }
                </div>
            </div>

            <div class="absolute right-5 top-5 h-10 w-100">
                <div class="inline-flex">
                    <div class="inline-flex">
                        <Menu as="div" className="relative inline-block text-left">
                            <div>
                                <Menu.Button className="inline-flex justify-center w-full rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-blue-500 text-sm font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-100 focus:ring-indigo-500">
                                    Change user
                                    <ChevronDownIcon className="-mr-1 ml-2 h-5 w-5" aria-hidden="true" />
                                </Menu.Button>
                            </div>

                            <Transition
                                as={Fragment}
                                enter="transition ease-out duration-100"
                                enterFrom="transform opacity-0 scale-95"
                                enterTo="transform opacity-100 scale-100"
                                leave="transition ease-in duration-75"
                                leaveFrom="transform opacity-100 scale-100"
                                leaveTo="transform opacity-0 scale-95"
                            >
                                <Menu.Items className="overflow-y-auto h-80 origin-top-right absolute right-0 mt-2 w-56 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 focus:outline-none">
                                    <div className="py-1">
                                        {
                                            users.map((user, index) => {
                                                return (<Menu.Item key={user.ssn}>
                                                    {({ active }) => (
                                                        <div
                                                            className={classNames(
                                                                active ? 'bg-gray-100 text-gray-900' : 'text-gray-700',
                                                                'block px-4 py-2 text-sm',
                                                                index === currentUser ? 'bg-blue-300' : '',
                                                            )}
                                                            onClick={() => { setCurrentUser(index) }}
                                                        >
                                                            {user.name + ' ' + user.surname + '-' + (user.isCustomer ? 'customer' : 'producer')}
                                                        </div>
                                                    )}
                                                </Menu.Item>
                                                );
                                            })
                                        }
                                    </div>
                                </Menu.Items>
                            </Transition>
                        </Menu>
                    </div>

                    <div class="inline-flex">
                        <Menu as="div" className="relative inline-block text-left">
                            <div>
                                <Menu.Button className="inline-flex justify-center w-full rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-blue-500 text-sm font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-100 focus:ring-indigo-500">
                                    Reports
                                    <ChevronDownIcon className="-mr-1 ml-2 h-5 w-5" aria-hidden="true" />
                                </Menu.Button>
                            </div>

                            <Transition
                                as={Fragment}
                                enter="transition ease-out duration-100"
                                enterFrom="transform opacity-0 scale-95"
                                enterTo="transform opacity-100 scale-100"
                                leave="transition ease-in duration-75"
                                leaveFrom="transform opacity-100 scale-100"
                                leaveTo="transform opacity-0 scale-95"
                            >
                                <Menu.Items className="origin-top-right absolute right-0 mt-2 w-56 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 focus:outline-none">
                                    <div className="py-1">
                                        {
                                            reports.map((report, index) => {
                                                return (<Menu.Item key={index}>
                                                    {({ active }) => (
                                                        <div
                                                            className={classNames(
                                                                active ? 'bg-gray-100 text-gray-900' : 'text-gray-700',
                                                                'block px-4 py-2 text-sm',
                                                            )}
                                                            onClick={() => { renderReport(index) }}
                                                        >
                                                            {report.name}
                                                        </div>
                                                    )}
                                                </Menu.Item>
                                                );
                                            })
                                        }
                                    </div>
                                </Menu.Items>
                            </Transition>
                        </Menu>
                    </div>

                    <div class="inline-flex">
                        <Menu as="div" className="relative inline-block text-left">
                            <div>
                                <Menu.Button className="inline-flex justify-center w-full rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-blue-500 text-sm font-medium text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-100 focus:ring-indigo-500">
                                    Menu
                                    <ChevronDownIcon className="-mr-1 ml-2 h-5 w-5" aria-hidden="true" />
                                </Menu.Button>
                            </div>

                            <Transition
                                as={Fragment}
                                enter="transition ease-out duration-100"
                                enterFrom="transform opacity-0 scale-95"
                                enterTo="transform opacity-100 scale-100"
                                leave="transition ease-in duration-75"
                                leaveFrom="transform opacity-100 scale-100"
                                leaveTo="transform opacity-0 scale-95"
                            >
                                <Menu.Items className="origin-top-right absolute right-0 mt-2 w-56 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 focus:outline-none">
                                    <div className="py-1">
                                        {
                                            menu.map((item, index) => {
                                                return (<Menu.Item key={index}>
                                                    {
                                                        ({ active }) => (
                                                            <div
                                                                className={classNames(
                                                                    active ? 'bg-gray-100 text-gray-900' : 'text-gray-700',
                                                                    'block px-4 py-2 text-sm',
                                                                )}
                                                                onClick={() => { setCurrentMenu(index) }}
                                                            >
                                                                {item.name}
                                                            </div>
                                                        )
                                                    }
                                                </Menu.Item>
                                                );
                                            })
                                        }
                                    </div>
                                </Menu.Items>
                            </Transition>
                        </Menu>
                    </div>
                </div>
                <div class="ml-1 inline-flex">
                    <div class="top-1 relative">
                        <button class="bg-blue-500 hover:bg-blue-700 py-2 px-4 text-white rounded" onClick={() => {
                            openShoppingCart(users, currentUser)
                        }}>
                            <svg class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                                <path d="M3 1a1 1 0 000 2h1.22l.305 1.222a.997.997 0 00.01.042l1.358 5.43-.893.892C3.74 11.846 4.632 14 6.414 14H15a1 1 0 000-2H6.414l1-1H14a1 1 0 00.894-.553l3-6A1 1 0 0017 3H6.28l-.31-1.243A1 1 0 005 1H3zM16 16.5a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0zM6.5 18a1.5 1.5 0 100-3 1.5 1.5 0 000 3z" />
                            </svg>
                        </button>
                    </div>
                </div>
            </div>
        </div >
    );
}

export default Header;