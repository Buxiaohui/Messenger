package bxh.msn.factory;

import androidx.annotation.NonNull;
import bxh.msn.BuildConfig;

public abstract class StateVerifier {
    private static final boolean DEBUG = false;
    private static final boolean DEBUGABLE = DEBUG && BuildConfig.DEBUG;

    private StateVerifier() {
    }

    @NonNull
    public static StateVerifier newInstance() {
        if (DEBUGABLE) {
            return new DebugStateVerifier();
        } else {
            return new DefaultStateVerifier();
        }
    }

    public abstract void throwIfRecycled();

    abstract void setRecycled(boolean isRecycled);

    private static class DefaultStateVerifier extends StateVerifier {
        private volatile boolean isReleased;

        DefaultStateVerifier() {
        }

        @Override
        public void throwIfRecycled() {
            if (isReleased) {
                throw new IllegalStateException("Already released");
            }
        }

        @Override
        public void setRecycled(boolean isRecycled) {
            this.isReleased = isRecycled;
        }
    }

    private static class DebugStateVerifier extends StateVerifier {
        // Keeps track of the stack trace where our state was set to recycled.
        private volatile RuntimeException recycledAtStackTraceException;

        DebugStateVerifier() {
        }

        @Override
        public void throwIfRecycled() {
            if (recycledAtStackTraceException != null) {
                throw new IllegalStateException("Already released", recycledAtStackTraceException);
            }
        }

        @Override
        void setRecycled(boolean isRecycled) {
            if (isRecycled) {
                recycledAtStackTraceException = new RuntimeException("Released");
            } else {
                recycledAtStackTraceException = null;
            }
        }
    }
}
