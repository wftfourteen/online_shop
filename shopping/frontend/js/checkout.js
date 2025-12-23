const { createApp } = Vue;
const { 
    ElContainer, ElHeader, ElMain, ElButton, ElInput, ElEmpty, ElAvatar, 
    ElDropdown, ElDropdownMenu, ElDropdownItem, ElIcon, ElMessage 
} = ElementPlus;
const { Check, Plus } = ElementPlusIconsVue;

function checkAuth() {
    const token = localStorage.getItem('token');
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    
    if (!token || userInfo.role !== 1) {
        window.location.href = './login.html';
        return null;
    }
    return userInfo;
}

createApp({
    components: {
        ElContainer,
        ElHeader,
        ElMain,
        ElButton,
        ElInput,
        ElEmpty,
        ElAvatar,
        ElDropdown,
        ElDropdownMenu,
        ElDropdownItem,
        ElIcon,
        Check,
        Plus
    },
    data() {
        return {
            userInfo: {},
            loading: false,
            submitting: false,
            addresses: [],
            selectedAddressId: null,
            orderItems: [],
            remark: '',
            shippingFee: 0 // 运费，满88免运费
        };
    },
    computed: {
        totalAmount() {
            return this.orderItems.reduce((sum, item) => {
                return sum + (item.price * item.quantity);
            }, 0);
        },
        finalAmount() {
            return this.totalAmount + this.shippingFee;
        }
    },
    mounted() {
        this.userInfo = checkAuth();
        if (this.userInfo) {
            this.loadAddresses();
            this.loadOrderItems();
            this.calculateShipping();
        }
    },
    methods: {
        async loadAddresses() {
            this.loading = true;
            try {
                const res = await API.getAddressList();
                if (res.code === 1 && res.data) {
                    this.addresses = res.data || [];
                    // 自动选择默认地址
                    const defaultAddr = this.addresses.find(addr => addr.isDefault === 1);
                    if (defaultAddr) {
                        this.selectedAddressId = defaultAddr.addressId;
                    } else if (this.addresses.length > 0) {
                        this.selectedAddressId = this.addresses[0].addressId;
                    }
                }
            } catch (error) {
                ElMessage.error('加载收货地址失败');
            } finally {
                this.loading = false;
            }
        },
        
        async loadOrderItems() {
            // 从sessionStorage获取订单商品
            const orderData = sessionStorage.getItem('orderData');
            const quickOrder = sessionStorage.getItem('quickOrder');
            
            if (orderData) {
                try {
                    const data = JSON.parse(orderData);
                    // 从购物车获取完整的商品信息
                    await this.loadCartItemsForOrder(data.items);
                    sessionStorage.removeItem('orderData');
                } catch (error) {
                    console.error('解析订单数据失败', error);
                    ElMessage.error('加载订单信息失败');
                }
            } else if (quickOrder) {
                // 立即购买的商品
                try {
                    const data = JSON.parse(quickOrder);
                    if (data.items && data.items.length > 0) {
                        await this.loadQuickOrderItems(data.items);
                    }
                    sessionStorage.removeItem('quickOrder');
                } catch (error) {
                    console.error('解析快速订单数据失败', error);
                    ElMessage.error('加载商品信息失败');
                }
            } else {
                ElMessage.warning('没有要结算的商品');
                setTimeout(() => {
                    window.location.href = './cart.html';
                }, 1500);
            }
        },
        
        async loadCartItemsForOrder(items) {
            try {
                // 获取购物车列表
                const cartRes = await API.getCartList();
                if (cartRes.code === 1 && cartRes.data) {
                    const cartItems = cartRes.data || [];
                    const orderItems = [];
                    
                    for (const item of items) {
                        const cartItem = cartItems.find(ci => ci.cartId === item.cartId || ci.productId === item.productId);
                        if (cartItem) {
                            orderItems.push({
                                productId: cartItem.productId,
                                name: cartItem.productName || cartItem.name,
                                mainImage: cartItem.mainImage || '/default-product.png',
                                price: cartItem.price,
                                quantity: item.quantity || cartItem.quantity,
                                cartId: cartItem.cartId // 保存cartId用于删除
                            });
                        } else {
                            // 如果购物车中没有，尝试从商品详情获取
                            try {
                                const productRes = await API.getProductDetail(item.productId);
                                if (productRes.code === 1 && productRes.data) {
                                    const product = productRes.data;
                                    orderItems.push({
                                        productId: product.productId,
                                        name: product.name,
                                        mainImage: product.images && product.images.length > 0 ? product.images[0] : '/default-product.png',
                                        price: product.price,
                                        quantity: item.quantity,
                                        cartId: item.cartId || null
                                    });
                                }
                            } catch (error) {
                                console.error('加载商品详情失败', error);
                            }
                        }
                    }
                    
                    this.orderItems = orderItems;
                }
            } catch (error) {
                ElMessage.error('加载购物车商品失败');
            }
        },
        
        async loadQuickOrderItems(items) {
            try {
                const orderItems = [];
                for (const item of items) {
                    const res = await API.getProductDetail(item.productId);
                    if (res.code === 1 && res.data) {
                        const product = res.data;
                        orderItems.push({
                            productId: product.productId,
                            name: product.name,
                            mainImage: product.images && product.images.length > 0 ? product.images[0] : '/default-product.png',
                            price: product.price,
                            quantity: item.quantity
                        });
                    }
                }
                this.orderItems = orderItems;
            } catch (error) {
                ElMessage.error('加载商品信息失败');
            }
        },
        
        calculateShipping() {
            // 满88免运费
            if (this.totalAmount >= 88) {
                this.shippingFee = 0;
            } else {
                this.shippingFee = 10;
            }
        },
        
        async submitOrder() {
            if (this.orderItems.length === 0) {
                ElMessage.warning('没有要购买的商品');
                return;
            }
            
            this.submitting = true;
            try {
                const orderData = {
                    addressId: this.selectedAddressId || null, // 地址可选
                    items: this.orderItems.map(item => ({
                        productId: item.productId,
                        quantity: item.quantity,
                        cartId: item.cartId // 如果有cartId，传递以便删除购物车
                    })),
                    remark: this.remark || '',
                    paymentMethod: 'alipay' // 默认支付方式
                };
                
                // 调用创建订单并支付接口
                const res = await API.createOrderAndPay(orderData);
                
                if (res.code === 1 && res.data) {
                    ElMessage.success('订单创建并支付成功！');
                    // 跳转到支付成功页面
                    sessionStorage.setItem('paymentSuccess', JSON.stringify(res.data));
                    setTimeout(() => {
                        window.location.href = './payment-success.html';
                    }, 1000);
                } else {
                    ElMessage.error(res.msg || '提交订单失败');
                    this.submitting = false;
                }
            } catch (error) {
                ElMessage.error('提交订单失败，请稍后重试');
                this.submitting = false;
            }
        },
        
        goToAddAddress() {
            ElMessage.info('添加地址功能请到个人中心设置');
        },
        
        handleCommand(command) {
            if (command === 'profile') {
                window.location.href = './profile.html';
            } else if (command === 'orders') {
                window.location.href = './orders.html';
            } else if (command === 'logout') {
                this.handleLogout();
            }
        },
        
        async handleLogout() {
            try {
                await API.logout();
            } catch (error) {}
            localStorage.removeItem('token');
            localStorage.removeItem('userInfo');
            window.location.href = './index.html';
        }
    },
    watch: {
        totalAmount() {
            this.calculateShipping();
        }
    }
}).use(ElementPlus).mount('#app');
