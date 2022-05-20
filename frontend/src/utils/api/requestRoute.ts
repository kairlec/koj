function wrapRecord<T extends Record<string, any>>(base: T, parent = ''): T {
  for (const key in base) {
    const value = base[key] as any
    if (typeof value === 'string') {
      if (value.length === 0) {
        ;(base[key] as any) = `${parent}`
      } else if (value.startsWith('/')) {
        ;(base[key] as any) = `${parent}${key}`
      } else {
        ;(base[key] as any) = `${parent}/${key}`
      }
    } else if (typeof value === 'object') {
      wrapRecord(value, `${parent}/${key}`)
    }
  }
  return base
}

export const apiRoute = wrapRecord({
  admin: {
    users: {
      _base: '',
      list() {
        return `${this._base}/-`
      },
      update(userId: string) {
        return `${this._base}/${userId}`
      },
    },
    problems: {
      _base: '',
      base() {
        return this._base
      },
      detail(id: string) {
        return `${this._base}/${id}`
      },
      withTag(problemId: string, tagId: string) {
        return `${this._base}/${problemId}/tags/${tagId}`
      },
      configList(problemId: string) {
        return `${this._base}/${problemId}/configs/-`
      },
      configs(problemId: string) {
        return `${this._base}/${problemId}/configs`
      },
      config(problemId: string, languageId: string) {
        return `${this._base}/${problemId}/configs/${languageId}`
      },
      runs(problemId: string) {
        return `${this._base}/${problemId}/runs`
      },
    },
    competitions: {
      _base: '',
      single(id: string) {
        return `${this._base}/${id}`
      },
      base() {
        return this._base
      },
      withProblem(competitionId: string, problemId: string) {
        return `${this._base}/${competitionId}/problems/${problemId}`
      },
    },
    tags: {
      _base: '',
      base() {
        return this._base
      },
      detail(tagId: string) {
        return `${this._base}/${tagId}`
      },
    },
  },
  users: {
    _base: '',
    destroySelf() {
      return `${this._base}`
    },
    self() {
      return `${this._base}/self`
    },
  },
  submits: {
    _base: '',
    request() {
      return `${this._base}`
    },
    detail(id: string) {
      return `${this._base}/${id}`
    },
  },
  competitions: {
    _base: '',
    join(id: string) {
      return `${this._base}/${id}:join`
    },
    submits(id: string) {
      return `${this._base}/${id}/submits/-`
    },
    problems(id: string) {
      return `${this._base}/${id}/problems/-`
    },
  },
  public: {
    competitions: {
      _base: '',
      list() {
        return `${this._base}/-`
      },
    },
    submits: {
      _base: '',
      list() {
        return `${this._base}/-`
      },
      languages: {
        _base: '',
        list() {
          return `${this._base}/-`
        },
      },
    },
    users: {
      _base: '',
      login() {
        return this._base
      },
      register() {
        return this._base
      },
      stat(usernameOrEmail: string) {
        return `${this._base}/${usernameOrEmail}`
      },
      rank() {
        return `${this._base}/rank`
      },
      pwd: {
        _base: '',
        forget() {
          return `${this._base}:forget`
        },
        reset() {
          return `${this._base}:reset`
        },
      },
    },
    problems: {
      _base: '',
      list() {
        return `${this._base}/-`
      },
      detail(id: string) {
        return `${this._base}/${id}`
      },
    },
    tags: {
      _base: '',
      list() {
        return `${this._base}/-`
      },
    },
  },
})

export default apiRoute
