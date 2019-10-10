package bxh.msn.factory;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.util.Pools;

/**
 * @Author:  buxiaohui
 * @Desc: 
 * @CreateDate: 2019-10-10 13:44
 **/
public final class FactoryPools {
    private static final String TAG = "FactoryPools";
    private static final int DEFAULT_POOL_SIZE = 20;
    private static final Resetter<Object> EMPTY_RESETTER = new Resetter<Object>() {
        @Override
        public void reset(@NonNull Object object) {
            // Do nothing.
        }
    };

    private FactoryPools() {
    }

    @NonNull
    public static <T extends Poolable> Pools.Pool<T> simple(int size, @NonNull Factory<T> factory) {
        return build(new Pools.SimplePool<T>(size), factory);
    }

    @NonNull
    public static <T extends Poolable> Pools.Pool<T> threadSafe(int size,
                                                                @NonNull Factory<T> factory) {
        return build(new Pools.SynchronizedPool<T>(size), factory);
    }

    @NonNull
    public static <T extends Poolable> Pools.Pool<T> threadSafe(int size,
                                                                @NonNull Factory<T> factory,
                                                                Resetter<T> resetter) {
        return build(new Pools.SynchronizedPool<T>(size), factory, resetter);
    }

    @NonNull
    public static <T> Pools.Pool<List<T>> threadSafeList() {
        return threadSafeList(DEFAULT_POOL_SIZE);
    }

    @NonNull
    public static <T> Pools.Pool<List<T>> threadSafeList(int size) {
        return build(new Pools.SynchronizedPool<List<T>>(size), new Factory<List<T>>() {
            @NonNull
            @Override
            public List<T> create() {
                return new ArrayList<>();
            }
        }, new Resetter<List<T>>() {
            @Override
            public void reset(@NonNull List<T> object) {
                object.clear();
            }
        });
    }

    @NonNull
    private static <T extends Poolable> Pools.Pool<T> build(@NonNull Pools.Pool<T> pool,
                                                            @NonNull Factory<T> factory) {
        return build(pool, factory, FactoryPools.<T>emptyResetter());
    }

    @NonNull
    public static <T> Pools.Pool<T> build(@NonNull Pools.Pool<T> pool, @NonNull Factory<T> factory,
                                          @NonNull Resetter<T> resetter) {
        return new FactoryPool<>(pool, factory, resetter);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    private static <T> Resetter<T> emptyResetter() {
        return (Resetter<T>) EMPTY_RESETTER;
    }

    public interface Factory<T> {
        T create();
    }

    public interface Resetter<T> {
        void reset(@NonNull T object);
    }

    public interface Poolable {
        @NonNull
        StateVerifier getVerifier();
    }

    private static final class FactoryPool<T> implements Pools.Pool<T> {
        private final Factory<T> factory;
        private final Resetter<T> resetter;
        private final Pools.Pool<T> pool;

        FactoryPool(@NonNull Pools.Pool<T> pool, @NonNull Factory<T> factory,
                    @NonNull Resetter<T> resetter) {
            this.pool = pool;
            this.factory = factory;
            this.resetter = resetter;
        }

        @Override
        public T acquire() {
            T result = pool.acquire();
            if (result == null) {
                result = factory.create();
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "Created new " + result.getClass());
                }
            }
            if (result instanceof Poolable) {
                ((Poolable) result).getVerifier().setRecycled(false /*isRecycled*/);
            }
            return result;
        }

        @Override
        public boolean release(@NonNull T instance) {
            if (instance instanceof Poolable) {
                ((Poolable) instance).getVerifier().setRecycled(true /*isRecycled*/);
            }
            resetter.reset(instance);
            return pool.release(instance);
        }
    }
}
