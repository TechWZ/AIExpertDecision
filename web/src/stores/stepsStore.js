import { defineStore } from 'pinia'

export const useStepsStore = defineStore('steps', {
  state: () => ({
    activeStep: 0
  }),
  actions: {
    setStep(step) {
      this.activeStep = step
    }
  }
})
