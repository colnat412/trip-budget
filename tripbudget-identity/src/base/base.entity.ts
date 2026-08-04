import { TrueFalseEnum } from 'src/config/enums/common.enum';
import {
  CreateDateColumn,
  UpdateDateColumn,
  Column,
  BaseEntity,
} from 'typeorm';

export abstract class BaseModel extends BaseEntity {
  @CreateDateColumn({
    name: 'created_at',
    type: 'timestamp',
  })
  createdAt: Date;

  @UpdateDateColumn({
    name: 'updated_at',
    type: 'timestamp',
  })
  updatedAt: Date;

  @Column({
    name: 'is_del',
    type: 'tinyint',
    default: 0,
    comment: '0 = active, 1 = deleted',
  })
  isDel: TrueFalseEnum;
}
