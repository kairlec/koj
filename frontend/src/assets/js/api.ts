import http from "./http";
import {stringify} from "qs"

type UserStat = {
    id: number,
    username: string,
    submitted: number,
    ac: number[],
    createTime: Date
}
type User = {
    id: number,
    username: string,
    email: string
    type: number,
    createTime: Date
}

interface IApi {
    registerUser(username: string, password: string, email: string): Promise<number>

    loginUser(usernameOrEmail: string, password: string): Promise<User>

    destroy(): Promise<undefined>

    stat(username: string): Promise<UserStat>

    existsUsernameOrEmail(usernameOrEmail: string): Promise<boolean>
}

// function logger(target: any, propertyKey: string, descriptor: PropertyDescriptor) {
//     const original = descriptor.value;
//
//     descriptor.value = function (...args: any[]) {
//         console.log('params: ', ...args);
//         const result = original.call(this, ...args);
//         console.log('result: ', result);
//         return result;
//     }
// }
//

let axios = http()

let api: IApi

;(() => {
    const _public = "/public"
    const _user = "/users"
    const login = `${_public}${_user}`
    const register = `${_public}${_user}`
    const destroy = `${_user}`
    const stat = `${_public}${_user}/`

    api = {
        registerUser: (username: string, password: string, email: string) => axios.put(register, stringify({
            username,
            password,
            email
        })),
        loginUser: (usernameOrEmail: string, password: string) => axios.post(login, stringify({
            usernameOrEmail,
            password
        })),
        destroy: () => axios.delete(destroy),
        existsUsernameOrEmail: (usernameOrEmail: string) => {
            // @ts-ignore
            return axios.head(`${stat}${usernameOrEmail}`, {ignoreError: true}).then(() => {
                return Promise.resolve(false)
            }).catch(error => {
                if (error.response.status == 409) {
                    return Promise.resolve(true)
                } else {
                    return Promise.reject(error)
                }
            })
        },
        stat: (username: string) => axios.get(`${stat}${username}`)
    }
})()

export default api