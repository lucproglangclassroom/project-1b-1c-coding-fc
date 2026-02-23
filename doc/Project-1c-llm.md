# LLM Interactions

## Summary

ChatGPT was used as a development assistant during the implementation of **Project 1c** for the TopWords project.

## How It Was Used

The LLM provided help with:

- Refactoring the sliding window logic into a purely functional implementation
- Designing the `WordStreamProcessor.clouds` pipeline using immutable `State` and `scanLeft`
- Managing word counts and the window without mutable collections
- Implementing command-line options `--every-k-steps` and `--min-frequency` in a functional style
- Ensuring correctness of emitted word clouds under various configurations
- Structuring state transitions and handling ignored/short words efficiently
- Suggesting unit test approaches for window trimming, step-based emission, and case-insensitive word merging

## Extra Credit Implemented

The following features were added as part of Project 1c:

1. **Immutable Sliding Window**  
   - Replaced mutable window logic with an immutable `Queue` and pure functions (`addWord`, `trimToWindow`) for state transitions.

2. **Case-Insensitive Word Counting**  
   - Words are normalized to lowercase if `ignoreCase` is enabled, merging counts of variants like `Apple` and `apple`.

3. **Update Word Cloud Every k Steps**  
   - Added the `everyKSteps` parameter to emit clouds only after every `k` accepted words, improving performance.

4. **Minimum Frequency Threshold**  
   - Added the `minFrequency` parameter to filter out words with counts below the threshold before emitting the cloud.

---

## Verification Process

All LLM suggestions were manually reviewed, adapted where necessary, compiled, and tested locally.  

The final implementation:

- Uses immutable data structures for the window and counts
- Produces clouds correctly under different `windowSize`, `cloudSize`, `minLength`, `ignoreCase`, `everyKSteps`, and `minFrequency` configurations
- Passes all MUnit tests for Project 1c

All design decisions and testing were completed independently.

## Conclusion

The LLM was used strictly as a support tool for guidance and debugging during Project 1c.  

All final code, functional design, and testing reflect my independent implementation of an immutable sliding-window word cloud processor.
