import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import Home from "../components/Home.vue";

const routes: Array<RouteRecordRaw> = [
    {
        path: "/home",
        name: "Home",
        component: Home,
    },
    // {
    //     path: "/about",
    //     name: "About",
    //     // route level code-splitting
    //     // this generates a separate chunk (about.[hash].js) for this route
    //     // which is lazy-loaded when the route is visited.
    //     component: () =>
    //         import(/* webpackChunkName: "About" */ "../components/About.vue")
    // }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;