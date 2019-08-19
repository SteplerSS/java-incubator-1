package boost.brain.course.tasks.repository;

import boost.brain.course.tasks.controller.dto.TaskDto;
import boost.brain.course.tasks.repository.entities.TaskEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class TasksRepository{

    private final EntityManager entityManager;

    @Autowired
    public TasksRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public TaskDto create(final TaskDto taskDto) {
        if (taskDto == null) {
            return null;
        }

        TaskEntity taskEntity = new TaskEntity();
        BeanUtils.copyProperties(taskDto, taskEntity, "id");
        entityManager.persist(taskEntity);

        TaskDto result = new TaskDto();
        BeanUtils.copyProperties(taskEntity, result);

        return result;
    }

    public TaskDto read(final long id) {
        TaskEntity taskEntity = entityManager.find(TaskEntity.class, id);
        if (taskEntity == null) {
            return null;
        }

        TaskDto result = new TaskDto();
        BeanUtils.copyProperties(taskEntity, result);

        return result;
    }

    public boolean update(final TaskDto taskDto) {
        if (taskDto == null) {
            return false;
        }

        TaskEntity taskEntity = entityManager.find(TaskEntity.class, taskDto.getId());
        if (taskEntity == null) {
            return false;
        }

        BeanUtils.copyProperties(taskDto, taskEntity, "createDate", "author");
        entityManager.merge(taskEntity);

        return true;
    }

    public boolean delete(final long id) {
        TaskEntity taskEntity = entityManager.find(TaskEntity.class, id);
        if (taskEntity == null) {
            return false;
        }

        entityManager.remove(taskEntity);
        return true;
    }

    public long count() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        countQuery.select(criteriaBuilder.count(countQuery.from(TaskEntity.class)));

        Long result = entityManager.createQuery(countQuery).getSingleResult();
        return result;
    }

    public List<TaskDto> getPage(final int pageNumber, final int pageSize) {
        List<TaskDto> result = new ArrayList<>();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<TaskEntity> criteriaQuery = criteriaBuilder.createQuery(TaskEntity.class);
        Root<TaskEntity> from = criteriaQuery.from(TaskEntity.class);
        CriteriaQuery<TaskEntity> select = criteriaQuery.select(from);

        TypedQuery<TaskEntity> typedQuery = entityManager.createQuery(select);
        typedQuery.setFirstResult((pageNumber - 1) * pageSize);
        typedQuery.setMaxResults(pageSize);

        List<TaskEntity> taskEntities = typedQuery.getResultList();

        for (TaskEntity taskEntity: taskEntities) {
            TaskDto taskDto = new TaskDto();
            BeanUtils.copyProperties(taskEntity, taskDto);
            result.add(taskDto);
        }
        return result;
    }
}