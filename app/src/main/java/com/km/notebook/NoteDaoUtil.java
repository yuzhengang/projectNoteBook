package com.km.notebook;

import java.util.List;

public class NoteDaoUtil {

    /**
     * 添加数据，如果有重复则覆盖
     *
     * @param noteBean
     */
    public static void insertNote(NoteBean noteBean) {
        NoteApplication.getDaoInstant().getNoteBeanDao().insertOrReplace(noteBean);
    }

    /**
     * 删除数据
     *
     * @param id
     */
    public static void deleteNote(long id) {
        NoteApplication.getDaoInstant().getNoteBeanDao().deleteByKey(id);
    }

    /**
     * 更新数据
     */
    public static void updateNote(NoteBean noteBean) {
        NoteApplication.getDaoInstant().getNoteBeanDao().update(noteBean);
    }


     public  static  List<NoteBean>  queryNoteByTitle(String   title){
         return NoteApplication.getDaoInstant().getNoteBeanDao().queryBuilder().where(NoteBeanDao.Properties.Title.like("%"+title+"%")).list();
     }


     public static  NoteBean   queryNoteById( Long   noteBeanId){
         return NoteApplication.getDaoInstant().getNoteBeanDao().load(noteBeanId);
     }


    /**
     * 查询所有数据
     *
     * @return
     */
    public static List<NoteBean> queryAll() {
        return NoteApplication.getDaoInstant().getNoteBeanDao().queryBuilder()
                .orderDesc(NoteBeanDao.Properties.Date).list();
    }

}
