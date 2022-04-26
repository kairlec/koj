import http from './http';
import { stringify } from 'qs';
import { Storage } from './storage';
import { AxiosPromise, AxiosResponse } from 'axios';

type UserStat = {
  id: number;
  username: string;
  submitted: number;
  ac: number[];
  createTime: Date;
};
type User = {
  id: number;
  username: string;
  email: string;
  type: number;
  createTime: Date;
};

interface IApi {
  registerUser(username: string, password: string, email: string): Promise<number>;

  loginUser(usernameOrEmail: string, password: string): Promise<User>;

  destroy(): Promise<undefined>;

  stat(username: string): Promise<UserStat>;

  existsUsernameOrEmail(usernameOrEmail: string): Promise<boolean>;

  self(): Promise<User>;
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

let axios = http();

let api: IApi;

function defaultExtra<T = any>(res: AxiosResponse<T>): T {
  return res.data;
}

function data<T = any>(promise: AxiosPromise<T>, extra: (res: AxiosResponse<T>) => T = defaultExtra): Promise<T> {
  return promise.then((res) => {
    return extra(res);
  });
}

(() => {
  const _public = '/public';
  const _user = '/users';
  const login = `${_public}${_user}`;
  const register = `${_public}${_user}`;
  const destroy = `${_user}`;
  const stat = `${_public}${_user}/`;
  const self = `${_user}/self`;

  api = {
    self() {
      return data(axios.get(self, { ignoreError: true }));
    },
    registerUser: (username: string, password: string, email: string) => {
      return data(
        axios.put(
          register,
          stringify({
            username,
            password,
            email
          })
        )
      );
    },
    loginUser: (usernameOrEmail: string, password: string) => {
      return data(
        axios.post(
          login,
          stringify({
            usernameOrEmail,
            password
          })
        ),
        (res) => {
          Storage.identity(res.headers[Storage.xIdentity]);
          return res.data;
        }
      );
    },
    destroy: () => axios.delete(destroy),
    existsUsernameOrEmail(usernameOrEmail: string) {
      return axios
        .head(`${stat}${usernameOrEmail}`, { ignoreError: true })
        .then(() => {
          return Promise.resolve(false);
        })
        .catch((error) => {
          if (error.response.status == 409) {
            return Promise.resolve(true);
          } else {
            return Promise.reject(error);
          }
        });
    },
    stat(username: string) {
      return data(axios.get(`${stat}${username}`));
    }
  };
})();

export default api;
